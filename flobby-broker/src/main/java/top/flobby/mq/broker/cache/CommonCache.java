package top.flobby.mq.broker.cache;

import top.flobby.mq.broker.config.GlobalProperties;
import top.flobby.mq.broker.core.CommitLogMMapFileModelManager;
import top.flobby.mq.broker.core.ConsumeQueueMMapFileModelManager;
import top.flobby.mq.broker.model.ConsumeQueueOffsetModel;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.netty.nameserver.HeartBeatTaskManager;
import top.flobby.mq.broker.netty.nameserver.NameServerClient;

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

    private static GlobalProperties globalProperties = new GlobalProperties();
    private static List<TopicModel> topicModelList = new ArrayList<>();
    private static ConsumeQueueOffsetModel consumeQueueOffsetModel = new ConsumeQueueOffsetModel();
    private static ConsumeQueueMMapFileModelManager consumeQueueMMapFileModelManager = new ConsumeQueueMMapFileModelManager();
    private static CommitLogMMapFileModelManager commitLogMMapFileModelManager = new CommitLogMMapFileModelManager();
    private static HeartBeatTaskManager heartBeatTaskManager = new HeartBeatTaskManager();

    public static HeartBeatTaskManager getHeartBeatTaskManager() {
        return heartBeatTaskManager;
    }

    public static NameServerClient getNameServerClient() {
        return nameServerClient;
    }

    private static NameServerClient nameServerClient = new NameServerClient();

    public static CommitLogMMapFileModelManager getCommitLogMMapFileModelManager() {
        return commitLogMMapFileModelManager;
    }

    public static void setCommitLogMMapFileModelManager(CommitLogMMapFileModelManager commitLogMMapFileModelManager) {
        CommonCache.commitLogMMapFileModelManager = commitLogMMapFileModelManager;
    }

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

    public static ConsumeQueueOffsetModel getConsumerQueueOffsetModel() {
        return consumeQueueOffsetModel;
    }

    public static void setConsumerQueueOffsetModel(ConsumeQueueOffsetModel consumeQueueOffsetModel) {
        CommonCache.consumeQueueOffsetModel = consumeQueueOffsetModel;
    }

    public static ConsumeQueueMMapFileModelManager getConsumeQueueMMapFileModelManager() {
        return consumeQueueMMapFileModelManager;
    }

    public static void setConsumeQueueMMapFileModelManager(ConsumeQueueMMapFileModelManager consumeQueueMMapFileModelManager) {
        CommonCache.consumeQueueMMapFileModelManager = consumeQueueMMapFileModelManager;
    }
}
