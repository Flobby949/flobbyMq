package top.flobby.mq.broker.model;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消费队列消费model
 * @create : 2025-07-30 16:59
 **/

public class ConsumeQueueConsumeReqModel {
    private String topic;
    private String consumeGroup;
    private Integer queueId;
    private Integer batchSize;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumeGroup() {
        return consumeGroup;
    }

    public void setConsumeGroup(String consumeGroup) {
        this.consumeGroup = consumeGroup;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
}
