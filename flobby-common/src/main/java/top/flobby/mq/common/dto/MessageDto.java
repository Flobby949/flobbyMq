package top.flobby.mq.common.dto;

import top.flobby.mq.common.enums.MessageSendWayEnum;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消息发送体
 * @create : 2025-07-24 16:05
 **/

public class MessageDto {
    private String topic;
    private int queueId;
    private String msgId;
    /**
     * 发送方式
     *
     * @see MessageSendWayEnum
     */
    private int sendWay;
    private byte[] body;

    public int getSendWay() {
        return sendWay;
    }

    public void setSendWay(int sendWay) {
        this.sendWay = sendWay;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "topic='" + topic + '\'' +
                ", queueId=" + queueId +
                ", msgId='" + msgId + '\'' +
                ", sendWay=" + sendWay +
                ", body=" + new String(body) +
                '}';
    }
}
