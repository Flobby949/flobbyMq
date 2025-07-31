package top.flobby.mq.client.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultMqConsumerTest {

    private DefaultMqConsumer consumer;

    @BeforeEach
    void setUp() throws InterruptedException{
        consumer = new DefaultMqConsumer();
        consumer.setNsIp("127.0.0.1");
        consumer.setNsPort(9093);
        consumer.setNsUser("flobby");
        consumer.setNsPassword("123456");
        consumer.setTopic("order_cancel_topic");
        consumer.setConsumeGroup("test_consume_group");
        consumer.setBatchSize(10);
        consumer.setMessageConsumeListener(messageList -> {
            messageList.forEach(message -> {
                System.out.println("消费端获取到的数据："+new String(message.getBody()));
            });
            return ConsumeResult.CONSUME_SUCCESS();
        });
        consumer.start();
    }

    @Test
    void start() {
    }
}