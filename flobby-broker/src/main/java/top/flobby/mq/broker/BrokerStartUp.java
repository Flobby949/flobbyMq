package top.flobby.mq.broker;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.config.ConsumerQueueOffsetLoader;
import top.flobby.mq.broker.config.GlobalPropertiesLoader;
import top.flobby.mq.broker.config.TopicModelInfoLoader;
import top.flobby.mq.broker.core.CommitLogAppendHandler;
import top.flobby.mq.broker.core.ConsumeQueueAppendHandler;
import top.flobby.mq.broker.model.TopicModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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


    public static void main(String[] args) throws IOException, InterruptedException {
        // 加载配置，缓存对象生成
        initProperties();
        // 模拟初始化文件映射
        String topic = "order_cancel_topic";
        for (int i = 0; i < 50; i++) {
            commitLogAppendHandler.appendMsg(topic, ("this is content " + i).getBytes());
            TimeUnit.SECONDS.sleep(1);
        }
        commitLogAppendHandler.readMsg(topic);
    }

    /**
     * 初始化配置
     */
    private static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();
        topicModelInfoLoader = new TopicModelInfoLoader();
        topicModelInfoLoader.loadProperties();
        topicModelInfoLoader.startRefreshMqTopicInfoTask();
        consumerQueueOffsetLoader = new ConsumerQueueOffsetLoader();
        consumerQueueOffsetLoader.loadProperties();
        consumerQueueOffsetLoader.startRefreshConsumerQueueOffsetTask();

        commitLogAppendHandler = new CommitLogAppendHandler();
        for (TopicModel topicModel : CommonCache.getTopicModelList()) {
            String topicName = topicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);
            consumeQueueAppendHandler.prepareConsumeQueue(topicName);
        }
    }
}
