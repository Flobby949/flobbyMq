package top.flobby.mq.broker;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.config.GlobalPropertiesLoader;
import top.flobby.mq.broker.config.TopicModelInfoLoader;
import top.flobby.mq.broker.core.CommitLogAppendHandler;
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

    public static void main(String[] args) throws IOException {
        // 加载配置，缓存对象生成
        initProperties();
        // 模拟初始化文件映射
        String topic = "order_cancel_topic";
        commitLogAppendHandler.appendMsg(topic, "broker start up".getBytes());
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
        commitLogAppendHandler = new CommitLogAppendHandler();
        for (TopicModel topicModel : CommonCache.getTopicModelMap().values()) {
            String topicName = topicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);
        }
    }
}
