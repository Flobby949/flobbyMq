package top.flobby.mq.broker.rebalance.strategy;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 重平衡接口
 * @create : 2025-07-25 15:55
 **/

public interface IReBalanceStrategy {
    void doReBalance(ReBalanceInfo reBalanceInfo);
}
