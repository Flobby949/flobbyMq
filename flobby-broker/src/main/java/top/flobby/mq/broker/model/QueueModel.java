package top.flobby.mq.broker.model;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 消息主题队列模型
 * @create : 2024-06-12 10:46
 **/

public class QueueModel {
    /**
     * id
     */
    private Integer id;
    /**
     * 当前存储的文件名
     */
    private String fileName;
    /**
     * 最早的索引，当前queue保存的数据记录的offset
     * 当第一条消息被消费后，过一段时间后删除第一条消息，即offset后移
     */
    private int lastOffset;
    /**
     * 最新的写入位置
     */
    private int latestOffset;
    /**
     * 最大偏移量
     */
    private int offsetLimit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(int lastOffset) {
        this.lastOffset = lastOffset;
    }

    public int getLatestOffset() {
        return latestOffset;
    }

    public void setLatestOffset(int latestOffset) {
        this.latestOffset = latestOffset;
    }

    public int getOffsetLimit() {
        return offsetLimit;
    }

    public void setOffsetLimit(int offsetLimit) {
        this.offsetLimit = offsetLimit;
    }

    @Override
    public String toString() {
        return "QueueModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", lastOffset=" + lastOffset +
                ", latestOffset=" + latestOffset +
                ", offsetLimit=" + offsetLimit +
                '}';
    }
}
