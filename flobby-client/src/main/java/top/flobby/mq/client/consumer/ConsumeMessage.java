package top.flobby.mq.client.consumer;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-25 10:59
 **/

public class ConsumeMessage {

    private byte[] body;

    private Integer queueId;

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
