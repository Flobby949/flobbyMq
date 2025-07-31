package top.flobby.mq.common.dto;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消费回调请求dto
 * @create : 2025-07-31 14:58
 **/

public class ConsumeMsgAckReqDto extends BaseBrokerRemoteDto{

    private String topic;
    private String consumeGroup;
    private Integer queueId;
    private Integer ackCount;
    private String ip;
    private Integer port;

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

    public Integer getAckCount() {
        return ackCount;
    }

    public void setAckCount(Integer ackCount) {
        this.ackCount = ackCount;
    }

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
}
