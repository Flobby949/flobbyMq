package top.flobby.mq.client.producer;

class DefaultProducerTest {

    public static void main(String[] args) {
        DefaultProducer defaultProducer = new DefaultProducer();
        defaultProducer.setNsIp("127.0.0.1");
        defaultProducer.setNsPort(9093);
        defaultProducer.setNsUser("flobby");
        defaultProducer.setNsPassword("123456");
        defaultProducer.start();
    }
}