package top.flobby.mq.client.consumer;

import java.util.List;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 数据消费监听器
 * @create : 2025-07-25 10:57
 **/

public interface MessageConsumeListener {

    /**
     * 默认的拉取消费函数
     *
     * @param messageList list
     */
    ConsumeResult consume(List<ConsumeMessage> messageList);
}
