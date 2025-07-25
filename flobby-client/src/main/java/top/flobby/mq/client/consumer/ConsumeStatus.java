package top.flobby.mq.client.consumer;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消费结果
 * @create : 2025-07-25 11:06
 **/

public enum ConsumeStatus {

    /**
     * 消费成功
     */
    CONSUME_SUCCESS,

    /**
     * 尚未消费完成
     */
    CONSUME_LATER,

    ;

}
