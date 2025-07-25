package top.flobby.mq.client.consumer;

import java.util.List;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 并发消息消费
 * @create : 2025-07-25 11:01
 **/

public class ConcurrentMessageConsumeListener implements MessageConsumeListener {
    @Override
    public ConsumeResult consume(List<ConsumeMessage> messageList) {
        return null;
    }
}
