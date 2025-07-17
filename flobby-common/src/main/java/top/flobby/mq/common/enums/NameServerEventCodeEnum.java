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
    MASTER_START_REPLICATION_ACK(5, "master节点开始复制"),
    MASTER_REPLICATION_MSG(6, "主从同步数据"),
    SLAVE_HEART_BEAT(7, "从节点心跳事件"),
    SLAVE_REPLICATION_ACK(8, "从节点复制消息成功"),

    NODE_REPLICATION_MSG(9, "链式复制节点同步数据"),
    NODE_REPLICATION_ACK_MSG(10, "链式复制数据同步ACK消息")
    ;

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
