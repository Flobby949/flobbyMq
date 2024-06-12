package top.flobby.mq.broker.config;

import io.netty.util.internal.StringUtil;
import top.flobby.mq.broker.cache.CommonCache;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 节点主题加载器
 * @create : 2024-06-12 10:07
 **/

public class TopicInfoLoader {

    private TopicInfo topicInfo;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("FLOBBY_MQ_HOME is inValid!");
        }
        String topicJsonFilePath = basePath + "/broker/config/flobbymq-topic.json";

        topicInfo = new TopicInfo();
    }
}
