package top.flobby.mq.broker.cache;

import top.flobby.mq.broker.config.GlobalProperties;
import top.flobby.mq.broker.core.ConsumeQueueMMapFileModelManager;
import top.flobby.mq.broker.model.ConsumerQueueOffsetModel;
import top.flobby.mq.broker.model.TopicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 统一缓存对象
 * @create : 2024-06-12 10:11
 **/

public class CommonCache {

    public static GlobalProperties globalProperties = new GlobalProperties();

    public static List<TopicModel> topicModelList = new ArrayList<>();

    public static ConsumerQueueOffsetModel consumerQueueOffsetModel = new ConsumerQueueOffsetModel();

    public static ConsumeQueueMMapFileModelManager consumeQueueMMapFileModelManager = new ConsumeQueueMMapFileModelManager();

    public static List<TopicModel> getTopicModelList() {
        return topicModelList;
    }

    public static Map<String, TopicModel> getTopicModelMap() {
        return topicModelList.stream().collect(Collectors.toMap(TopicModel::getTopic, item -> item));
    }

    public static void setTopicModelList(List<TopicModel> topicModelList) {
        CommonCache.topicModelList = topicModelList;
    }

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }

    public static ConsumerQueueOffsetModel getConsumerQueueOffsetModel() {
        return consumerQueueOffsetModel;
    }

    public static void setConsumerQueueOffsetModel(ConsumerQueueOffsetModel consumerQueueOffsetModel) {
        CommonCache.consumerQueueOffsetModel = consumerQueueOffsetModel;
    }

    public static ConsumeQueueMMapFileModelManager getConsumeQueueMMapFileModelManager() {
        return consumeQueueMMapFileModelManager;
    }

    public static void setConsumeQueueMMapFileModelManager(ConsumeQueueMMapFileModelManager consumeQueueMMapFileModelManager) {
        CommonCache.consumeQueueMMapFileModelManager = consumeQueueMMapFileModelManager;
    }
}
