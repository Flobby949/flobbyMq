package top.flobby.mq.broker.rebalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.rebalance.strategy.IReBalanceStrategy;
import top.flobby.mq.broker.rebalance.strategy.ReBalanceInfo;
import top.flobby.mq.broker.rebalance.strategy.impl.RandomReBalanceStrategyImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
            List<ConsumerInstance> consumerInstanceList = consumerInstanceMap.getOrDefault(topic, new ArrayList<>());
            for (ConsumerInstance consumerInstance : consumerInstanceList) {
                if (consumerInstance.getConsumerReqId().equals(instance.getConsumerReqId())) {
                    return;
                }
            }
            consumerInstanceList.add(instance);
            consumerInstanceMap.put(topic, consumerInstanceList);
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
            reBalanceInfo.getChangeConsumerGroupMap().clear();;
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
        }
    }
}
