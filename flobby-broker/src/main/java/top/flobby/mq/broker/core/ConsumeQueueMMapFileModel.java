package top.flobby.mq.broker.core;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.lock.PutMessageLock;
import top.flobby.mq.broker.lock.UnFailReentrantLock;
import top.flobby.mq.broker.model.CommitLogModel;
import top.flobby.mq.broker.model.QueueModel;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.utils.LogFileNameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : consumeQueue 的 MMap 文件映射模型
 * @create : 2024-07-15 09:49
 **/

public class ConsumeQueueMMapFileModel {

    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private ByteBuffer readBuffer;
    private FileChannel fileChannel;
    private String topic;
    private Integer queueId;
    private String consumeQueueFileName;
    private PutMessageLock putMessageLock;

    /**
     * 每一段msg的长度都是12byte，写一个常量管理
     */
    public static final Integer CONSUME_QUEUE_UNIT_SIZE = 12;

    /**
     * 在 MMAP 中加载文件
     *
     * @param topicName         主题名称
     * @param queueId           队列 ID
     * @param startOffset       起始偏移量
     * @param latestWriteOffset 最新写入偏移量
     * @param mappedSize        映射大小
     * @throws IOException io异常
     */
    public void loadFileInMMap(String topicName, int queueId, int startOffset, int latestWriteOffset, int mappedSize) throws IOException {
        // 持久化topicName
        this.topic = topicName;
        this.queueId = queueId;
        String filePath = this.getLatestConsumeQueueFilePath();
        this.doMMap(filePath, startOffset, latestWriteOffset, mappedSize);
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
    private void doMMap(String filePath, int startOffset, int latestWriteOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        // 文件不存在，抛出异常
        if (!file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + "inValid！");
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
        // 构造一个新的buffer，独立于写入的buffer
        readBuffer = mappedByteBuffer.slice();
        // 重新定位到即将写入数据的位置
        mappedByteBuffer.position(latestWriteOffset);
    }

    /**
     * 获取最新消费队列文件路径
     *
     * @return {@link String }
     */
    private String getLatestConsumeQueueFilePath() {
        TopicModel topicModel = CommonCache.getTopicModelMap().get(this.topic);
        if (topicModel == null) {
            throw new IllegalArgumentException("topic is inValid! topicName=" + this.topic);
        }
        List<QueueModel> queueList = topicModel.getQueueList();
        QueueModel queueModel = queueList.get(this.queueId);
        if (queueModel == null) {
            throw new IllegalArgumentException("queue is inValid! queueId = " + this.queueId);
        }
        CommitLogModel commitLog = topicModel.getLatestCommitLog();
        long diff = commitLog.countDiff();
        String filePath = "";
        if (diff == 0) {
            // 已经写满
            filePath = this.createNewConsumeQueueFile(queueModel.getFileName());
        } else if (diff > 0) {
            // 还有机会写入
            filePath = LogFileNameUtil.buildConsumeQueueFilePath(this.topic, this.queueId, queueModel.getFileName());
        }
        // System.out.println("latestCommitQueueFilePath=" + filePath);
        return filePath;
    }

    /**
     * 构造新的 ConsumerQueue 文件
     *
     * @param oldFileName 旧文件名
     * @return {@link String }
     */
    private String createNewConsumeQueueFile(String oldFileName) {
        String newFileName = LogFileNameUtil.incrConsumeQueueFileName(oldFileName);
        String newFilePath = LogFileNameUtil.buildConsumeQueueFilePath(this.topic, this.queueId, newFileName);
        File newCommitFile = new File(newFilePath);
        try {
            newCommitFile.createNewFile();
            System.out.println("创建了新的 ConsumeQueue 文件, newFileName = " + newFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newFilePath;
    }

    /**
     * 编写内容
     *
     * @param content 内容
     * @param force   力
     */
    public void writeContent(byte[] content, boolean force) {
        try {
            putMessageLock.lock();
            mappedByteBuffer.put(content);
            if (force) {
                mappedByteBuffer.force();
            }
        } catch (Exception e) {

        } finally {
            putMessageLock.unlock();
        }
    }

    public void writeContent(byte[] content) {
        this.writeContent(content, false);
    }

    /**
     * 阅读内容
     *
     * @param position
     * @return {@link byte[] }
     */
    public byte[] readContent(Integer position) {
        // 独立打开一个readBuffer，线程之间相互独立，不影响其他线程
        ByteBuffer readBuf = readBuffer.slice();
        readBuf.position(position);
        // 每一段msg的长度都是12byte
        byte[] content = new byte[CONSUME_QUEUE_UNIT_SIZE];
        readBuf.get(content);
        return content;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getQueueId() {
        return queueId;
    }
}
