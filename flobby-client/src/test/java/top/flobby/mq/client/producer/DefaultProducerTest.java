package top.flobby.mq.client.producer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.flobby.mq.common.dto.MessageDto;

class DefaultProducerTest {

    private DefaultProducerImpl defaultProducer;

    @BeforeEach
    public void setUp() {
        defaultProducer = new DefaultProducerImpl();
        defaultProducer.setNsIp("127.0.0.1");
        defaultProducer.setNsPort(9093);
        defaultProducer.setNsUser("flobby");
        defaultProducer.setNsPassword("123456");
        defaultProducer.start();
    }

    @Test
    void sendSyncMsg() {
        MessageDto message = new MessageDto();
        message.setTopic("test_topic");
        message.setBody("this is a async content".getBytes());
        defaultProducer.sendAsync(message);
        message.setBody("this is a test content".getBytes());
        SendResult send = defaultProducer.send(message);
        System.out.println(send.getSendStatus().name());
    }
}