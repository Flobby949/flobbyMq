package top.flobby.mq.broker.model;

import java.util.List;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 消息topic映射对象
 * @create : 2024-06-12 10:44
 **/

public class TopicModel {
    /**
     * 主题
     */
    private String topic;
    /**
     * 队列
     */
    private List<QueueModel> queueList;
    /**
     * 创建时间
     */
    private Long createAt;
    /**
     * 更新时间
     */
    private Long updateAt;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<QueueModel> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<QueueModel> queueList) {
        this.queueList = queueList;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "TopicModel{" +
                "topic='" + topic + '\'' +
                ", queueList=" + queueList +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
