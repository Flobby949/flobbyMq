package top.flobby.mq.broker.core;

import com.alibaba.fastjson2.JSON;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.constant.BrokerConstants;
import top.flobby.mq.broker.lock.PutMessageLock;
import top.flobby.mq.broker.lock.UnFailReentrantLock;
import top.flobby.mq.broker.model.*;
import top.flobby.mq.broker.utils.LogFileNameUtil;
import top.flobby.mq.common.dto.MessageDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 基础 mmap 对象模型
 * @create : 2024-06-12 09:42
 **/

public class CommitLogMMapFileModel {

    private String topic;
    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private ByteBuffer readByteBuffer;
    private FileChannel fileChannel;
    private PutMessageLock putMessageLock;

    /**
     * 支持指定 offset 的文件映射
     * 结束映射 offset - 开始映射 offset = 映射的内存体积
     *
     * @param topicName   topic
     * @param startOffset 起始偏移量
     * @param mappedSize  映射体积
     */
    public void loadFileInMMap(String topicName, int startOffset, int mappedSize) throws IOException {
        // 持久化topicName
        this.topic = topicName;
        String filePath = getLatestCommitLogFilePath(topicName);
        this.doMMap(filePath, startOffset, mappedSize);
        // 接口模式，配置非公平锁
        putMessageLock = new UnFailReentrantLock();
    }

    /**
     * 映射 mmap 文件
     *
     * @param filePath    文件路径
     * @param startOffset 起始偏移量
     * @param mappedSize  映射大小
     * @throws IOException io异常
     */
    private void doMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        // 文件不存在，抛出异常
        if (!file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + " inValid！");
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
        readByteBuffer = mappedByteBuffer.slice();
        // 定位到最新的offset
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topic);
        mappedByteBuffer.position(topicModel.getLatestCommitLog().getOffset().get());
    }

    /**
     * 获取最新 CommitLog 文件路径
     *
     * @param topicName 主题名称
     * @return {@link String }
     */
    private String getLatestCommitLogFilePath(String topicName) {
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topicName);
        if (topicModel == null) {
            throw new IllegalArgumentException("topic is inValid! topicName=" + topicName);
        }
        CommitLogModel latestCommitLog = topicModel.getLatestCommitLog();
        long diff = latestCommitLog.countDiff();
        String filePath = "";
        if (diff == 0) {
            // 已经写满
            filePath = this.createNewCommitLogFile(topicName, latestCommitLog).getNewFilePath();
        } else if (diff > 0) {
            // 还有机会写入
            filePath = LogFileNameUtil.buildCommitLogFilePath(topicName, latestCommitLog.getFileName());
        }
        // System.out.println("latestCommitLogFilePath=" + latestCommitLog.getFileName());
        return filePath;
    }

    /**
     * 创建新 CommitLog 文件
     *
     * @param topicName topic
     * @param oldFile   旧文件
     * @return {@link CommitLogFilePath }
     */
    private CommitLogFilePath createNewCommitLogFile(String topicName, CommitLogModel oldFile) {
        String newFileName = LogFileNameUtil.incrCommitLogFileName(oldFile.getFileName());
        String newFilePath = LogFileNameUtil.buildCommitLogFilePath(topicName, newFileName);
        File newCommitFile = new File(newFilePath);
        try {
            newCommitFile.createNewFile();
            System.out.println("创建了新的 CommitLog 文件, newFileName = " + newFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new CommitLogFilePath(newFileName, newFilePath);
    }

    /**
     * 文件从指定的 offset 开始读取
     *
     * @param readOffset 读取偏移量
     * @param len       大小
     * @return {@link byte[] }
     */
    public byte[] readContent(int readOffset, int len) {
        ByteBuffer readBuf = readByteBuffer.slice();
        readBuf.position(readOffset);
        byte[] content = new byte[len];
        readBuf.get(content);
        return content;
    }

    /**
     * 写入数据
     *
     * @param message            消息内容
     * @param force                 是否强制刷盘
     */
    public void writeContent(MessageDto message, boolean force) throws IOException {
        /**
         * 1. 定位到最新的 commitLog 中
         * 2. 判断是否写满，如果写满，自动创建新的文件，并且做新的 mmap 映射
         * 3. 对 content 内容封装，再判断写入是否会写满，如果不会，则选择当前 commitLog，否则再创建新的 commitLog 并且映射
         * 4. 追加写入，刷盘
         *
         * 思考？
         * 1. 性能优化问题：需要定义一个新的对象专门管理各个 topic 的最新写入 offset 值，并且定时刷新到磁盘中，是否需要用 mmap ？
         * 2. 线程安全问题：写入数据，offset 变更，在高并发场景下，offset 是否会被多个线程访问？如何选择锁？
         */
        // 更新 offset 准备工作
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topic);
        if (topicModel == null) {
            throw new IllegalArgumentException("topic is null! topicName=" + topic);
        }
        CommitLogModel commitLog = topicModel.getLatestCommitLog();
        if (commitLog == null) {
            throw new IllegalArgumentException("commitLog is null!");
        }
        // 加锁
        putMessageLock.lock();
        // 把对象转换成 byte 数组，将 size 转换为 byte 数组，然后拼上 content
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setContent(message.getBody());
        byte[] messageBytes = commitLogMessageModel.convertToBytes();
        // 判断剩余空间是否足够写入
        this.checkCommitLogHasEnableSpace(commitLogMessageModel);
        // 默认刷到 page cache 中（异步）
        mappedByteBuffer.put(messageBytes);
        // 消息写入 ConsumerQueue
        AtomicInteger currentMsgOffset = commitLog.getOffset();
        this.dispatcher(message, currentMsgOffset.get());
        // 更新 offset
        commitLog.getOffset().addAndGet(messageBytes.length);
        if (force) {
            // 强制刷盘
            mappedByteBuffer.force();
        }
        putMessageLock.unlock();
    }

    public void writeContent(MessageDto message) throws IOException {
        this.writeContent(message, false);
    }

    /**
     * 消息写入 consumerQueue
     *
     * @param message         写入内容
     * @param msgIndex       消息指针
     */
    private void dispatcher(MessageDto message, int msgIndex) {
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topic);
        if (topicModel == null) {
            throw new IllegalArgumentException("topic is undefined! topicName=" + topic);
        }
        ConsumeQueueDetailModel consumerQueueDetail = new ConsumeQueueDetailModel();
        consumerQueueDetail.setCommitLogFileName(Integer.parseInt(topicModel.getLatestCommitLog().getFileName()));
        consumerQueueDetail.setMsgLength(message.getBody().length);
        consumerQueueDetail.setMsgIndex(msgIndex);
        System.out.println("写入 consumeQueue 内容：" +JSON.toJSONString(consumerQueueDetail));
        byte[] contentArr = consumerQueueDetail.convertToBytes();
        consumerQueueDetail.convertToModel(contentArr);
        // System.out.println("从byte中转换 consumeQueue 内容：" +JSON.toJSONString(consumerQueueDetail));
        // TODO 暂时还没传递queueId
        int queueId;
        if (message.getQueueId() >= 0) {
            queueId = message.getQueueId();
        } else {
            queueId = 0;
        }
        List<ConsumeQueueMMapFileModel> queueModelList = CommonCache.getConsumeQueueMMapFileModelManager().get(this.topic);
        ConsumeQueueMMapFileModel consumeQueueMMapFileModel = queueModelList
                .stream()
                .filter(queueModel -> queueModel.getQueueId().equals(queueId))
                .findFirst()
                .orElse(null);
        consumeQueueMMapFileModel.writeContent(contentArr);
        // 刷新 offset
        QueueModel queueModel = topicModel.getQueueList().get(queueId);
        queueModel.getLatestOffset().addAndGet(contentArr.length);
    }

    /**
     * 检查CommitLog是否足够保存这条消息
     *
     * @param commitLogMessageModel 提交日志消息模型
     * @throws IOException io异常
     */
    private void checkCommitLogHasEnableSpace(CommitLogMessageModel commitLogMessageModel) throws IOException {
        TopicModel topicModel = CommonCache.getTopicModelMap().get(this.topic);
        CommitLogModel latestCommitLog = topicModel.getLatestCommitLog();
        // 剩余空间
        long freeSpace = latestCommitLog.countDiff();
        // 如果空间不足，创建新的 CommitLog 文件并做映射
        if (!(freeSpace >= commitLogMessageModel.convertToBytes().length)) {
            CommitLogFilePath commitLogFile = this.createNewCommitLogFile(this.topic, latestCommitLog);
            // 重置 offset
            latestCommitLog.setFileName(commitLogFile.getNewFileName());
            latestCommitLog.setOffset(new AtomicInteger(0));
            latestCommitLog.setOffsetLimit(Long.valueOf(BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE));
            // 映射新 mmap
            this.doMMap(commitLogFile.getNewFilePath(), BrokerConstants.MMAP_DEFAULT_START_OFFSET, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
        }
    }

    /**
     * 释放资源，推荐方式
     * 通过反射安全机制释放内存资源
     */
    public void clean() {
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0) {
            return;
        }
        // 调用 DirectByteBuffer 类中的 Cleaner 属性中的 clean 方法
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    /**
     * 反射机制调用方法
     *
     * @param target     目标
     * @param methodName 方法名称
     * @param args       参数
     * @return {@link Object }
     */
    public Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    /**
     * 反射获取方法
     *
     * @param target     目标
     * @param methodName 方法名称
     * @param args       参数
     * @return {@link Method }
     * @throws NoSuchMethodException 异常
     */
    private Method method(Object target, String methodName, Class<?>[] args) throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    /**
     * 解析 ByteBuffer 中的 attachment 属性
     *
     * @param buffer 缓冲区
     * @return {@link ByteBuffer }
     */
    private ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";
        Method[] methods = buffer.getClass().getMethods();
        for (Method method : methods) {
            if ("attachment".equals(method.getName())) {
                methodName = "attachment";
                break;
            }
        }
        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        return viewedBuffer == null ? buffer : viewed(buffer);
    }

    class CommitLogFilePath {
        private String newFileName;
        private String newFilePath;

        public CommitLogFilePath(String newFileName, String newFilePath) {
            this.newFileName = newFileName;
            this.newFilePath = newFilePath;
        }

        public String getNewFileName() {
            return newFileName;
        }

        public void setNewFileName(String newFileName) {
            this.newFileName = newFileName;
        }

        public String getNewFilePath() {
            return newFilePath;
        }

        public void setNewFilePath(String newFilePath) {
            this.newFilePath = newFilePath;
        }
    }
}
