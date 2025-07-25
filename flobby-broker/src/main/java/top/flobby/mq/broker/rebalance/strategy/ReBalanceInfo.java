package top.flobby.mq.broker.rebalance.strategy;

import top.flobby.mq.broker.rebalance.ConsumerInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 重平衡信息
 * @create : 2025-07-25 15:54
 **/

public class ReBalanceInfo {

    private Map<String, List<ConsumerInstance>> consumerInstanceMap;

    private Map<String, Set<String>> changeConsumerGroupMap = new HashMap<>();

    public Map<String, Set<String>> getChangeConsumerGroupMap() {
        return changeConsumerGroupMap;
    }

    public void setChangeConsumerGroupMap(Map<String, Set<String>> changeConsumerGroupMap) {
        this.changeConsumerGroupMap = changeConsumerGroupMap;
    }

    public Map<String, List<ConsumerInstance>> getConsumerInstanceMap() {
        return consumerInstanceMap;
    }

    public void setConsumerInstanceMap(Map<String, List<ConsumerInstance>> consumerInstanceMap) {
        this.consumerInstanceMap = consumerInstanceMap;
    }
}
