package top.flobby.mq.common.enums;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 注册中心响应code枚举
 * @create : 2025-04-30 16:50
 **/

public enum NameServerResponseCodeEnum {
    REGISTRY_SUCCESS(1001, "服务注册成功"),
    UN_REGISTRY_SERVICE(1002, "服务正常下线"),
    HEART_BEAT_SUCCESS(1003, "心跳消息正常"),
    PULL_BROKER_ADDRESS_SUCCESS(1004, "拉取Broker地址成功"),

    ERROR_ACCESS(4001, "账号验证异常"),
    ;

    final int code;
    final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    NameServerResponseCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
