package top.flobby.mq.broker.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.netty.util.internal.StringUtil;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.constant.BrokerConstants;
import top.flobby.mq.broker.model.ConsumeQueueOffsetModel;
import top.flobby.mq.broker.utils.FileContentUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : ConsumerQueue的offset配置加载器
 * @create : 2024-06-13 10:38
 **/

public class ConsumerQueueOffsetLoader {

    private String filePath;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("FLOBBY_MQ_HOME is inValid!");
        }
        filePath = basePath + "/config/consume_queue-offset.json";
        String fileContent = FileContentUtil.readFromFile(filePath);
        ConsumeQueueOffsetModel consumeQueueOffsetModel = JSON.parseObject(fileContent, ConsumeQueueOffsetModel.class);
        CommonCache.setConsumerQueueOffsetModel(consumeQueueOffsetModel);
    }

    /**
     * 刷盘定时任务
     */
    public void startRefreshConsumerQueueOffsetTask() {
        CommonThreadPoolConfig.refreshConsumerQueueOffsetExecutor.execute(() -> {
            do {
                try {
                    // 因为刚启动时从磁盘中同步到内存，不需要立刻刷盘，先休眠一段时间
                    TimeUnit.SECONDS.sleep(BrokerConstants.DEFAULT_REFRESH_CONSUMER_QUEUE_OFFSET_INTERVAL);
                    System.out.println("ConsumerQueueOffset 写入磁盘");
                    ConsumeQueueOffsetModel consumeQueueOffsetModel = CommonCache.getConsumerQueueOffsetModel();
                    FileContentUtil.overwriteToFile(filePath, JSON.toJSONString(consumeQueueOffsetModel, JSONWriter.Feature.PrettyFormat));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
        });
    }
}
