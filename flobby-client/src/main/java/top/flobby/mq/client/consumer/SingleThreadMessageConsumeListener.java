package top.flobby.mq.client.consumer;

import java.util.List;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 单线程消息消费监听器
 * @create : 2025-07-25 11:00
 **/

public class SingleThreadMessageConsumeListener implements MessageConsumeListener{
    @Override
    public ConsumeResult consume(List<ConsumeMessage> messageList) {
        return null;
    }
}
