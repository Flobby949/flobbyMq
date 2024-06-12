package top.flobby.mq.broker.config;

import com.alibaba.fastjson2.JSON;
import io.netty.util.internal.StringUtil;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.utils.FileContentReaderUtil;

import java.util.List;
import java.util.stream.Collectors;

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
        String fileContent = FileContentReaderUtil.readFromFile(topicJsonFilePath);
        List<TopicModel> topicModels = JSON.parseArray(fileContent, TopicModel.class);
        CommonCache.setTopicModelMap(topicModels.stream().collect(Collectors.toMap(TopicModel::getTopic, item -> item)));
    }
}
