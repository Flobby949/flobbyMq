package top.flobby.mq.common.enums;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : broker响应code
 * @create : 2025-07-25 09:44
 **/

public enum BrokerResponseCodeEnum {

    SEND_MSG_RESP(2001, "推送消息给broker，响应code"),
    CONSUME_MSG_RESP(2002, "消费broker消息返回数据，响应code"),

    ;

    final int code;
    final String desc;

    BrokerResponseCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
}
