package top.flobby.mq.broker.rebalance.strategy.impl;

import org.apache.commons.collections4.CollectionUtils;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.rebalance.ConsumerInstance;
import top.flobby.mq.broker.rebalance.strategy.IReBalanceStrategy;
import top.flobby.mq.broker.rebalance.strategy.ReBalanceInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : range模式（kafka）
 * @create : 2025-07-25 15:56
 **/

public class RangeReBalanceStrategyImpl implements IReBalanceStrategy {

    @Override
    public void doReBalance(ReBalanceInfo reBalanceInfo) {
        Map<String, List<ConsumerInstance>> consumeInstanceMap = reBalanceInfo.getConsumerInstanceMap();
        Map<String, TopicModel> topicModelMap = CommonCache.getTopicModelMap();
        for (String topic : consumeInstanceMap.keySet()) {
            List<ConsumerInstance> consumerInstances = consumeInstanceMap.get(topic);
            if (CollectionUtils.isEmpty(consumerInstances)) {
                continue;
            }
            // 每个消费组实例
            Map<String, List<ConsumerInstance>> consumeGroupMap = consumerInstances.stream()
                    .collect(Collectors.groupingBy(ConsumerInstance::getConsumeGroup));
            TopicModel topicModel = topicModelMap.get(topic);
            int queueSize = topicModel.getQueueList().size();
            for (String consumerGroup : consumeGroupMap.keySet()) {
                List<ConsumerInstance> consumerInstanceList = consumeGroupMap.get(consumerGroup);
                // 算出每个消费者平均拥有多少条队列
                int eachConsumerQueueNum = queueSize / consumerInstanceList.size();
                int queueId = 0;
                for (ConsumerInstance instance : consumerInstanceList) {
                    for (int queueNums = 0; queueNums < eachConsumerQueueNum; queueNums++) {
                        instance.getQueueIdList().add(queueId++);
                    }
                }
                // 代表有多余队列没有被用到
                int remainQueueCount = queueSize - queueId;
                if (remainQueueCount > 0) {
                    for (int i = 0; i < remainQueueCount; i++) {
                        consumerInstanceList.get(i).getQueueIdList().add(queueId++);
                    }
                }
            }
        }
    }

}