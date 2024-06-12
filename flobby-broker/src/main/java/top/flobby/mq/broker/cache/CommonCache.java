package top.flobby.mq.broker.cache;

import top.flobby.mq.broker.config.GlobalProperties;
import top.flobby.mq.broker.model.TopicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 统一缓存对象
 * @create : 2024-06-12 10:11
 **/

public class CommonCache {

    public static GlobalProperties globalProperties = new GlobalProperties();

    public static List<TopicModel> topicModelList = new ArrayList<TopicModel>();

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }

    public static List<TopicModel> getTopicModelList() {
        return topicModelList;
    }

    public static void setTopicModelList(List<TopicModel> topicModelList) {
        CommonCache.topicModelList = topicModelList;
    }
}
