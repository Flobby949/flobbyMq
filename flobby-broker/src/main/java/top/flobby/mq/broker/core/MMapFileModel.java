package top.flobby.mq.broker.core;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.constant.BrokerConstants;
import top.flobby.mq.broker.model.CommitLogMessageModel;
import top.flobby.mq.broker.model.CommitLogModel;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.utils.CommitLogFileNameUtil;

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

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 基础 mmap 对象模型
 * @create : 2024-06-12 09:42
 **/

public class MMapFileModel {

    private String topic;
    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;

    /**
     * 支持指定 offset 的文件映射
     * 结束映射 offset - 开始映射 offset = 映射的内存体积
     *
     * @param topicName    topic
     * @param startOffset 起始偏移量
     * @param mappedSize  映射体积
     */
    public void loadFileInMMap(String topicName, long startOffset, int mappedSize) throws IOException {
        // 持久化topicName
        this.topic = topicName;
        String filePath = getLatestCommitLogFilePath(topicName);
        this.doMMap(filePath, startOffset, mappedSize);
    }

    /**
     * 映射 mmap 文件
     *
     * @param filePath    文件路径
     * @param startOffset 起始偏移量
     * @param mappedSize  映射大小
     * @throws IOException io异常
     */
    private void doMMap(String filePath, long startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        // 文件不存在，抛出异常
        if (!file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + "inValid！");
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
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
        long diff = latestCommitLog.getOffsetLimit() - latestCommitLog.getOffset();
        String filePath = "";
        if (diff == 0) {
            // 已经写满
            filePath = this.createNewCommitLogFile(topicName, latestCommitLog);
        } else if (diff > 0) {
            // 还有机会写入
            filePath = CommonCache.getGlobalProperties().getMqHome()
                    + BrokerConstants.BASE_STORE_PATH
                    + topicName
                    + latestCommitLog.getFileName();
        }
        return filePath;
    }

    /**
     * 创建新 CommitLog 文件
     *
     * @param topicName topic
     * @param oldFile   旧文件
     * @return {@link String }
     */
    private String createNewCommitLogFile(String topicName, CommitLogModel oldFile) {
        String newFileName = CommitLogFileNameUtil.incrCommitLogFileName(oldFile.getFileName());
        String newFilePath = CommonCache.getGlobalProperties().getMqHome()
                + BrokerConstants.BASE_STORE_PATH
                + topicName
                + newFileName;
        File newCommitFile = new File(newFilePath);
        try {
            newCommitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newFilePath;
    }

    /**
     * 文件从指定的 offset 开始读取
     *
     * @param readOffset 读取偏移量
     * @param size       大小
     * @return {@link byte[] }
     */
    public byte[] readContent(int readOffset, int size) {
        // 定位到指定 offset
        mappedByteBuffer.position(readOffset);
        byte[] content = new byte[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            // 从内存中访问，效率非常高
            byte b = mappedByteBuffer.get(readOffset + i);
            content[j++] = b;
        }
        return content;
    }

    /**
     * 写入数据
     *
     * @param commitLogMessageModel 消息内容
     * @param force                                 是否强制刷盘
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel, boolean force) throws IOException {
        /**
         * TODO
         * 1. 定位到最新的 commitLog 中
         * 2. 判断是否写满，如果写满，自动创建新的文件，并且做新的 mmap 映射
         * 3. 对 content 内容封装，再判断写入是否会写满，如果不会，则选择当前 commitLog，否则再创建新的 commitLog 并且映射
         * 4. 追加写入，刷盘
         *
         * 思考？
         * 1. 性能优化问题：需要定义一个新的对象专门管理各个 topic 的最新写入 offset 值，并且定时刷新到磁盘中，是否需要用 mmap ？
         * 2. 线程安全问题：写入数据，offset 变更，在高并发场景下，offset 是否会被多个线程访问？如何选择锁？
         */

        // 把对象转换成 byte 数组，将 size 转换为 byte 数组，然后拼上 content
        byte[] messageBytes = commitLogMessageModel.convertToByte();
        // 判断剩余空间是否足够写入
        this.checkCommitLogHasEnableSpace(commitLogMessageModel);
        // 默认刷到 page cache 中（异步）
        mappedByteBuffer.put(messageBytes);
        if (force) {
            // 强制刷盘
            mappedByteBuffer.force();
        }
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
        long freeSpace = latestCommitLog.getOffsetLimit() - latestCommitLog.getOffset();
        // 如果空间不足，创建新的 CommitLog 文件并做映射
        if (!(freeSpace >= commitLogMessageModel.getSize())) {
            String newCommitLogFile = this.createNewCommitLogFile(this.topic, latestCommitLog);
            // 映射新 mmap
            this.doMMap(newCommitLogFile, BrokerConstants.MMAP_DEFAULT_START_OFFSET, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
        }
    }

    public void writeContent(CommitLogMessageModel commitLogMessageModel) throws IOException {
        this.writeContent(commitLogMessageModel, false);
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
}
