package top.flobby.mq.client.producer;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消息发送结果
 * @create : 2025-07-24 16:11
 **/

public class SendResult {

    private SendStatus sendStatus;

    public SendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }
}
