package top.flobby.mq.broker.core;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.model.ConsumerQueueOffsetModel;
import top.flobby.mq.broker.model.TopicModel;

import java.util.Map;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 消费队列，消费处理器
 * @create : 2024-07-15 11:26
 **/

public class ConsumeQueueConsumeHandler {

    /**
     * 读取最新一条ConsumerQueue消息内容
     *
     * @return {@link byte[] }
     */
    public byte[] consume(String topic, String consumeGroup, Integer queueId) {
        // 检查参数合法性
        // 获取当前匹配的消费队列的最新的consumeQueue的offset
        // 获取当前匹配的队列存储文件的mmap对象，读取offset地址数据
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topic);
        if (topicModel == null) {
            throw new IllegalArgumentException("topic 【" + topic + "】is null");
        }
        ConsumerQueueOffsetModel.OffsetTable offsetTable = CommonCache.getConsumerQueueOffsetModel().getOffsetTable();
        Map<String, ConsumerQueueOffsetModel.ConsumerGroupDetail> topicConsumerGroupDetailMap = offsetTable.getTopicConsumerGroupDetail();
        ConsumerQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = topicConsumerGroupDetailMap.get(topic);
        // 如果是首次消费，初始化
        if (consumerGroupDetail == null) {
            consumerGroupDetail = new ConsumerQueueOffsetModel.ConsumerGroupDetail();
            topicConsumerGroupDetailMap.put(topic, consumerGroupDetail);
        }
        Map<String, Map<String, String>> consumeGroupOffsetMap = consumerGroupDetail.getConsumerGroupDetailMap();
        Map<String, String> queueOffsetDetail = consumeGroupOffsetMap.get(consumeGroup);

        return null;

    }

    /**
     * 更新 ConsumerQueue-offset 值
     * @return
     */
    public boolean ack() {

        return true;
    }
}
