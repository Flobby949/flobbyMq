package top.flobby.mq.broker.cache;

import top.flobby.mq.broker.config.GlobalProperties;
import top.flobby.mq.broker.config.TopicInfo;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 统一缓存对象
 * @create : 2024-06-12 10:11
 **/

public class CommonCache {

    public static GlobalProperties globalProperties = new GlobalProperties();

    public static TopicInfo topicInfo = new TopicInfo();

    public static TopicInfo getTopicInfo() {
        return topicInfo;
    }

    public static void setTopicInfo(TopicInfo topicInfo) {
        CommonCache.topicInfo = topicInfo;
    }

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }
}
