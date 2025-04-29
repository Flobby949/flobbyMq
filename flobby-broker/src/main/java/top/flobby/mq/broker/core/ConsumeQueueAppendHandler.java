package top.flobby.mq.broker.core;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.model.QueueModel;
import top.flobby.mq.broker.model.TopicModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description :
 * @create : 2024-07-15 09:53
 **/

public class ConsumeQueueAppendHandler {

    public void prepareConsumeQueue (String topicName) throws IOException {
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topicName);
        List<QueueModel> queueList = topicModel.getQueueList();
        List<ConsumeQueueMMapFileModel> consumeQueueMMapFileModels = new ArrayList<>();
        // 循環遍歷，MMap初始化
        for (QueueModel queue : queueList) {
            // 创建消费队列文件
            ConsumeQueueMMapFileModel consumeQueueMMapFileModel = new ConsumeQueueMMapFileModel();
            consumeQueueMMapFileModel.loadFileInMMap(
                    topicName,
                    queue.getId(),
                    queue.getLastOffset(),
                    queue.getLatestOffset().get(),
                    queue.getOffsetLimit()
            );
            consumeQueueMMapFileModels.add(consumeQueueMMapFileModel);
        }
        CommonCache.getConsumeQueueMMapFileModelManager().put(topicName, consumeQueueMMapFileModels);

    }
}
