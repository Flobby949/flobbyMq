package top.flobby.mq.broker;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.config.GlobalPropertiesLoader;
import top.flobby.mq.broker.config.TopicModelInfoLoader;
import top.flobby.mq.broker.constant.BrokerConstants;
import top.flobby.mq.broker.core.MessageAppendHandler;
import top.flobby.mq.broker.model.TopicModel;

import java.io.IOException;
import java.util.List;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : broker 启动类
 * @create : 2024-06-12 10:52
 **/

public class BrokerStartUp {

    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static TopicModelInfoLoader topicModelInfoLoader;
    private static MessageAppendHandler messageAppendHandler;

    public static void main(String[] args) throws IOException {
        // 加载配置，缓存对象生成
        initProperties();
        // 模拟初始化文件映射
        String topic = "order_cancel_topic";
        messageAppendHandler.appendMsg(topic, "broker start up");
        messageAppendHandler.readMsg(topic);
    }

    /**
     * 初始化配置
     */
    private static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();
        topicModelInfoLoader = new TopicModelInfoLoader();
        topicModelInfoLoader.loadProperties();
        messageAppendHandler = new MessageAppendHandler();
        List<TopicModel> topicModelList = CommonCache.getTopicModelList();
        for (TopicModel topicModel : topicModelList) {
            String topicName = topicModel.getTopic();
            String filePath = CommonCache.getGlobalProperties().getMqHome()
                    + BrokerConstants.BASE_STORE_PATH
                    + topicName + "/"
                    + "00000000";
            messageAppendHandler.prepareMMapLoading(filePath, topicName);
        }
    }
}
