package top.flobby.mq.client.producer;

import top.flobby.mq.common.dto.MessageDto;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-24 16:05
 **/

public interface Producer {

    /**
     * 同步发送
     *
     * @param message 消息
     * @return {@link SendResult }
     */
    SendResult send(MessageDto message);

    /**
     * 异步发送
     *
     * @param message 消息
     */
    void sendAsync(MessageDto message);

}
