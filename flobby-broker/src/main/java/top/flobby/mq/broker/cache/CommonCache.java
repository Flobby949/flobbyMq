package top.flobby.mq.broker.cache;

import top.flobby.mq.broker.config.GlobalProperties;
import top.flobby.mq.broker.model.TopicModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 统一缓存对象
 * @create : 2024-06-12 10:11
 **/

public class CommonCache {

    public static GlobalProperties globalProperties = new GlobalProperties();


    public static Map<String, TopicModel> topicModelMap = new HashMap<>();

    public static Map<String, TopicModel> getTopicModelMap() {
        return topicModelMap;
    }

    public static void setTopicModelMap(Map<String, TopicModel> topicModelMap) {
        CommonCache.topicModelMap = topicModelMap;
    }

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }
}
