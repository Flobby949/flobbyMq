package top.flobby.mq.broker;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.config.ConsumerQueueOffsetLoader;
import top.flobby.mq.broker.config.GlobalPropertiesLoader;
import top.flobby.mq.broker.config.TopicModelInfoLoader;
import top.flobby.mq.broker.core.CommitLogAppendHandler;
import top.flobby.mq.broker.core.ConsumeQueueAppendHandler;
import top.flobby.mq.broker.core.ConsumeQueueConsumeHandler;
import top.flobby.mq.broker.model.TopicModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : broker 启动类
 * @create : 2024-06-12 10:52
 **/

public class BrokerStartUp {

    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static TopicModelInfoLoader topicModelInfoLoader;
    private static CommitLogAppendHandler commitLogAppendHandler;
    private static ConsumerQueueOffsetLoader consumerQueueOffsetLoader;
    private static ConsumeQueueAppendHandler consumeQueueAppendHandler;
    private static ConsumeQueueConsumeHandler consumeQueueConsumeHandler;


    public static void main(String[] args) throws IOException, InterruptedException {
        // 加载配置，缓存对象生成
        initProperties();
        // 模拟初始化文件映射
        //模拟初始化文件映射
        String topic = "order_cancel_topic";
        String userServiceConsumeGroup = "user_service_group";
        String orderServiceConsumeGroup = "order_service_group";
        new Thread(() -> {
            while (true) {
                byte[] content = consumeQueueConsumeHandler.consume(topic, userServiceConsumeGroup, 0);
                if (content != null && content.length != 0) {
                    System.out.println(userServiceConsumeGroup + ",消费内容:" + new String(content));
                    consumeQueueConsumeHandler.ack(topic, userServiceConsumeGroup, 0);
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                byte[] content = consumeQueueConsumeHandler.consume(topic, orderServiceConsumeGroup, 0);
                if (content != null) {
                    System.out.println(orderServiceConsumeGroup + ",消费内容:" + new String(content));
                    consumeQueueConsumeHandler.ack(topic, orderServiceConsumeGroup, 0);
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        AtomicInteger i = new AtomicInteger();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                try {
                    commitLogAppendHandler.appendMsg(topic, ("message_" + (i.getAndIncrement())).getBytes());
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        System.out.println("开始多线程消费验证");
    }

    /**
     * 初始化配置
     */
    private static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        topicModelInfoLoader = new TopicModelInfoLoader();
        consumerQueueOffsetLoader = new ConsumerQueueOffsetLoader();
        commitLogAppendHandler = new CommitLogAppendHandler();
        consumeQueueAppendHandler = new ConsumeQueueAppendHandler();
        consumeQueueConsumeHandler = new ConsumeQueueConsumeHandler();

        globalPropertiesLoader.loadProperties();
        topicModelInfoLoader.loadProperties();
        topicModelInfoLoader.startRefreshMqTopicInfoTask();
        consumerQueueOffsetLoader.loadProperties();
        consumerQueueOffsetLoader.startRefreshConsumerQueueOffsetTask();

        for (TopicModel topicModel : CommonCache.getTopicModelList()) {
            String topicName = topicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);
            consumeQueueAppendHandler.prepareConsumeQueue(topicName);
        }
    }
}
