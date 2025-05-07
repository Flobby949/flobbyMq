package top.flobby.mq.common.enums;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 注册中心事件code枚举
 * @create : 2025-04-30 11:24
 **/

public enum NameServerEventCodeEnum {

    REGISTRY(1, "注册事件"),
    UN_REGISTRY(2, "下线事件"),
    HEART_BEAT(3, "心跳事件"),

    START_REPLICATION(4, "开启复制"),
    MASTER_REPLICATION_MSG(5, "主从同步数据"),
    SLAVE_HEART_BEAT(6, "从节点心跳事件");

    final int code;
    final String desc;

    NameServerEventCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static NameServerEventCodeEnum getByCode(int code) {
        for (NameServerEventCodeEnum eventCodeEnum : values()) {
            if (eventCodeEnum.getCode() == code) {
                return eventCodeEnum;
            }
        }
        return null;
    }
}
