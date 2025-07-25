package top.flobby.mq.client.consumer;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消费结果
 * @create : 2025-07-25 10:58
 **/

public class ConsumeResult {

    /**
     * @see ConsumeStatus
     */
    private int consumeResultStatus;

    public int getConsumeResultStatus() {
        return consumeResultStatus;
    }

    public void setConsumeResultStatus(int consumeResultStatus) {
        this.consumeResultStatus = consumeResultStatus;
    }

    public ConsumeResult(int consumeResultStatus) {
        this.consumeResultStatus = consumeResultStatus;
    }

    public static ConsumeResult CONSUME_SUCCESS() {
        return new ConsumeResult(ConsumeStatus.CONSUME_SUCCESS.ordinal());
    }

    public static ConsumeResult CONSUME_LATER() {
        return new ConsumeResult(ConsumeStatus.CONSUME_LATER.ordinal());
    }


}
