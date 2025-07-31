package top.flobby.mq.common.dto;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消费消息请求dto
 * @create : 2025-07-30 16:31
 **/

public class ConsumeMsgReqDto extends BaseBrokerRemoteDto {
    private String topic;
    private String consumeGroup;
    private String ip;
    private int port;
    private int batchSize;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String toString() {
        return "ConsumeMsgReqDto{" +
                "topic='" + topic + '\'' +
                ", consumeGroup='" + consumeGroup + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
