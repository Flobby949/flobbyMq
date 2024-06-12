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
     * 最小偏移量
     */
    private Long minOffset;
    /**
     * 当前偏移
     */
    private Long currentOffset;
    /**
     * 最大偏移量
     */
    private Long maxOffset;

    @Override
    public String toString() {
        return "QueueModel{" +
                "id=" + id +
                ", minOffset=" + minOffset +
                ", currentOffset=" + currentOffset +
                ", maxOffset=" + maxOffset +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getMinOffset() {
        return minOffset;
    }

    public void setMinOffset(Long minOffset) {
        this.minOffset = minOffset;
    }

    public Long getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(Long currentOffset) {
        this.currentOffset = currentOffset;
    }

    public Long getMaxOffset() {
        return maxOffset;
    }

    public void setMaxOffset(Long maxOffset) {
        this.maxOffset = maxOffset;
    }
}
