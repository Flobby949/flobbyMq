package top.flobby.mq.broker.rebalance;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消费者实例
 * @create : 2025-07-25 14:17
 **/

public class ConsumerInstance {
    private String ip;
    private Integer port;
    /**
     * 所属的消费组
     */
    private String consumeGroup;
    /**
     * 消费主题
     */
    private String topic;
    /**
     * 持有队列，一个消费者可以持有多个队列
     */
    private List<Integer> queueIdList = new ArrayList<>();
    /**
     * 消费者唯一id
     */
    private String consumerReqId;
    /**
     * 一次拉取多少条数据
     */
    private int batchSize;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getConsumeGroup() {
        return consumeGroup;
    }

    public void setConsumeGroup(String consumeGroup) {
        this.consumeGroup = consumeGroup;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Integer> getQueueIdList() {
        return queueIdList;
    }

    public void setQueueIdList(List<Integer> queueIdList) {
        this.queueIdList = queueIdList;
    }

    public String getConsumerReqId() {
        return consumerReqId;
    }

    public void setConsumerReqId(String consumerReqId) {
        this.consumerReqId = consumerReqId;
    }
}
