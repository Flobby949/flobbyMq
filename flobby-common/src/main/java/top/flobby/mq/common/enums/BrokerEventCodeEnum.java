package top.flobby.mq.common.enums;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : Broker服务端消息事件
 * @create : 2025-07-24 16:24
 **/

public enum BrokerEventCodeEnum {
    PUSH_MSG(1001, "推送消息"),
    CONSUME_MSG(1002, "消费消息"),
    CONSUME_SUCCESS(1003, "消费成功"),
    ;

    final int code;
    final String desc;

    BrokerEventCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
