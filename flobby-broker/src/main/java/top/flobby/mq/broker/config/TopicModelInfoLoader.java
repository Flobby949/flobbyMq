package top.flobby.mq.broker.config;

import com.alibaba.fastjson2.JSON;
import io.netty.util.internal.StringUtil;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.utils.FileContentReaderUtils;

import java.util.List;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 节点主题加载器
 * @create : 2024-06-12 10:07
 **/

public class TopicModelInfoLoader {

    private TopicModel topicModelInfo;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("FLOBBY_MQ_HOME is inValid!");
        }
        String topicJsonFilePath = basePath + "/config/flobbymq-topic.json";
        String fileContent = FileContentReaderUtils.readFromFile(topicJsonFilePath);
        List<TopicModel> topicModels = JSON.parseArray(fileContent, TopicModel.class);
        CommonCache.setTopicModelList(topicModels);
    }
}
