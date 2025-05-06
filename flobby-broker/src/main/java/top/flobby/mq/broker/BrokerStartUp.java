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
        // 初始化nameserver服务器通道
        initNameServerChannel();
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

    /**
     * 初始化nameserver服务器通道
     */
    private static void initNameServerChannel() {
        CommonCache.getNameServerClient().initConnect();
        CommonCache.getNameServerClient().sendRegistryMsg();
    }
}
