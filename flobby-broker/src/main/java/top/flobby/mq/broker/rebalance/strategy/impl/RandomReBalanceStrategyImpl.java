package top.flobby.mq.broker.rebalance.strategy.impl;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.rebalance.ConsumerInstance;
import top.flobby.mq.broker.rebalance.strategy.IReBalanceStrategy;
import top.flobby.mq.broker.rebalance.strategy.ReBalanceInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 随机重平衡
 * @create : 2025-07-25 15:56
 **/

public class RandomReBalanceStrategyImpl implements IReBalanceStrategy {

    @Override
    public void doReBalance(ReBalanceInfo reBalanceInfo) {
        Map<String, TopicModel> topicModelMap = CommonCache.getTopicModelMap();
        Map<String, List<ConsumerInstance>> consumeInstanceMap = reBalanceInfo.getConsumerInstanceMap();
        consumeInstanceMap.forEach((topic, consumerInstanceList) -> {
            // 该topic的信息
            TopicModel topicModel = topicModelMap.get(topic);
            // 获取每个topic消费组下的所有实例信息
            Map<String, List<ConsumerInstance>> consumeGroupMap = consumerInstanceList.stream()
                    .collect(Collectors.groupingBy(ConsumerInstance::getConsumeGroup));
            // 获取topic下的队列
            int queueSize = topicModel.getQueueList().size();
            //取出当前topic有变更过的消费组名
            Set<String> changeConsumerGroup = reBalanceInfo.getChangeConsumerGroupMap().get(topic);
            Map<String, List<ConsumerInstance>> consumeGroupHoldMap = new HashMap<>();
            for (String consumeGroup : consumeGroupMap.keySet()) {
                //变更的消费组名单中没包含当前消费组，不触发重平衡
                if (!changeConsumerGroup.contains(consumeGroup)) {
                    //依旧保存之前的消费组信息
                    consumeGroupHoldMap.put(consumeGroup, consumeGroupMap.get(consumeGroup));
                    continue;
                }
                // 持有queue的实例
                List<ConsumerInstance> holdQueueInstanceList = new ArrayList<>();
                List<ConsumerInstance> consumerInstances = consumeGroupMap.get(consumeGroup);
                // 获取每个消费组下的消费者数量
                int consumeSize = consumerInstances.size();
                Collections.shuffle(consumerInstances);
                if (queueSize >= consumeSize) {
                    // 队列数量大于等于消费者个数，每个消费者都可以持有队列
                    int queueId = 0;
                    // 第一遍遍历消费者，确保所有的消费者都有队列
                    for (int consumerIndex = 0; consumerIndex < consumeSize; consumerIndex++, queueId++) {
                        ConsumerInstance consumerInstance = consumerInstances.get(consumerIndex);
                        consumerInstance.getQueueIdList().add(queueId);
                        holdQueueInstanceList.add(consumerInstance);
                    }
                    // 便利剩下的queue队列，随机分配给消费者
                    for (; queueId < queueSize; queueId++) {
                        Random random = new Random();
                        // 在消费者中随机找一个
                        int randomConsumerIndex = random.nextInt(consumeSize);
                        ConsumerInstance consumerInstance = consumerInstances.get(randomConsumerIndex);
                        consumerInstance.getQueueIdList().add(queueId);
                        holdQueueInstanceList.add(consumerInstance);
                    }
                } else {
                    // 队列数小于消费者个数，有些消费者可能没有队列持有
                    for (int queueId = 0; queueId < queueSize; queueId++) {
                        ConsumerInstance consumerInstance = consumerInstances.get(queueId);
                        consumerInstance.getQueueIdList().add(queueId);
                        holdQueueInstanceList.add(consumerInstance);
                    }
                }
                consumeGroupHoldMap.put(consumeGroup, holdQueueInstanceList);
            }
            CommonCache.getConsumeHoldMap().put(topic, consumeGroupHoldMap);
        });
    }
}
