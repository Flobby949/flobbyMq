package top.flobby.mq.broker.rebalance;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.rebalance.strategy.IReBalanceStrategy;
import top.flobby.mq.broker.rebalance.strategy.ReBalanceInfo;
import top.flobby.mq.broker.rebalance.strategy.impl.RandomReBalanceStrategyImpl;
import top.flobby.mq.common.utils.AssertUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消费实例池
 * @create : 2025-07-25 14:19
 **/

public class ConsumerInstancePool {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerInstancePool.class);

    /**
     * topic -> consumerInstance
     */
    private Map<String, List<ConsumerInstance>> consumerInstanceMap = new ConcurrentHashMap<>();
    private static Map<String, IReBalanceStrategy> reBalanceStrategyMap = new HashMap<>();
    private ReBalanceInfo reBalanceInfo = new ReBalanceInfo();

    static {
        reBalanceStrategyMap.put("random", new RandomReBalanceStrategyImpl());
        reBalanceStrategyMap.put("range", new RandomReBalanceStrategyImpl());
    }

    /**
     * 加入实例池
     *
     * @param instance 实例
     */
    public void addInstancePool(ConsumerInstance instance) {
        synchronized (this) {
            String topic = instance.getTopic();
            //校验topic是否合法
            TopicModel topicModel = CommonCache.getTopicModelMap().get(topic);
            AssertUtil.isNotNull(topicModel,"topic非法");
            List<ConsumerInstance> consumerInstanceList = consumerInstanceMap.getOrDefault(topic, new ArrayList<>());
            for (ConsumerInstance consumerInstance : consumerInstanceList) {
                // 防止重复添加
                if (consumerInstance.getConsumerReqId().equals(instance.getConsumerReqId())) {
                    return;
                }
            }
            // 添加实例
            consumerInstanceList.add(instance);
            consumerInstanceMap.put(topic, consumerInstanceList);
            // 添加一个消费组
            Set<String> consumerGroupSet = reBalanceInfo.getChangeConsumerGroupMap().getOrDefault(topic, new HashSet<>());
            consumerGroupSet.add(instance.getConsumeGroup());
            reBalanceInfo.getChangeConsumerGroupMap().put(topic, consumerGroupSet);
        }
    }

    /**
     * 消费者重平衡
     * 定时任务触发，把已有的队列分配给消费者
     */
    public void doReBalance() {
        synchronized (this) {
            String reBalanceStrategy = CommonCache.getGlobalProperties().getRebalanceStrategy();
            //触发重平衡行为，根据参数决定重平衡策略的不同
            reBalanceInfo.setConsumerInstanceMap(this.consumerInstanceMap);
            reBalanceStrategyMap.get(reBalanceStrategy).doReBalance(reBalanceInfo);
            reBalanceInfo.getChangeConsumerGroupMap().clear();
            LOGGER.info("重平衡结束");
        }
    }

    /**
     * 从实例池中删除
     *
     * @param instance 实例
     */
    public void removeFromInstancePool(ConsumerInstance instance) {
        synchronized (this) {
            String topic = instance.getTopic();
            List<ConsumerInstance> consumerInstanceList = consumerInstanceMap.getOrDefault(topic, new ArrayList<>());
            List<ConsumerInstance> filterConsumerInstanceList = consumerInstanceList.stream()
                    .filter(item -> !instance.getConsumerReqId().equals(item.getConsumerReqId()))
                    .collect(Collectors.toList());
            consumerInstanceMap.put(topic, filterConsumerInstanceList);
            Set<String> consumerGroupSet = reBalanceInfo.getChangeConsumerGroupMap().get(topic);
            if (CollectionUtils.isEmpty(consumerGroupSet)) {
                return;
            }
            consumerGroupSet.remove(instance.getConsumeGroup());
        }
    }

    public void startReBalanceTask() {
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    doReBalance();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
