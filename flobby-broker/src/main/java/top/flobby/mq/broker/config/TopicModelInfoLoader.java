package top.flobby.mq.broker.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.netty.util.internal.StringUtil;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.constant.BrokerConstants;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.utils.FileContentUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 节点主题加载器
 * @create : 2024-06-12 10:07
 **/

public class TopicModelInfoLoader {

    private String filePath;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("FLOBBY_MQ_HOME is inValid!");
        }
        filePath = basePath + "/config/flobbymq-topic.json";
        String fileContent = FileContentUtil.readFromFile(filePath);
        List<TopicModel> topicModels = JSON.parseArray(fileContent, TopicModel.class);
        CommonCache.setTopicModelList(topicModels);
    }

    /**
     * 自动刷新 MQ 主题信息定时任务
     * 异步线程，每隔 xx 秒将内存配置刷新到磁盘中
     * 类似 Redis 的 RDB 设计思路
     */
    public void startRefreshMqTopicInfoTask() {
        CommonThreadPoolConfig.refreshMqTopicExecutor.execute(() -> {
            do {
                try {
                    // 因为刚启动时从磁盘中同步到内存，不需要立刻刷盘，先休眠一段时间
                    TimeUnit.SECONDS.sleep(BrokerConstants.DEFAULT_REFRESH_MQ_TOPIC_INTERVAL);
                    System.out.println("CommitLog 写入磁盘");
                    List<TopicModel> topicModelList = CommonCache.getTopicModelList();
                    FileContentUtil.overwriteToFile(filePath, JSON.toJSONString(topicModelList, JSONWriter.Feature.PrettyFormat));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
        });
    }
}
