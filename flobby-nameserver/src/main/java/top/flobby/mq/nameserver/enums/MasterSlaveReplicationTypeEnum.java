package top.flobby.mq.nameserver.enums;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 主从复制类型枚举
 * @create : 2025-05-08 09:58
 **/

public enum MasterSlaveReplicationTypeEnum {

    SYNC("sync", "同步"),
    HALF_SYNC("half_sync", "半同步"),
    ASYNC("async", "异步");

    final String type;
    final String desc;

    MasterSlaveReplicationTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static MasterSlaveReplicationTypeEnum of(String type) {
        for (MasterSlaveReplicationTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
