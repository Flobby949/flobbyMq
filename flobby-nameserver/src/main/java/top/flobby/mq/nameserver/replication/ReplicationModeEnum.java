package top.flobby.mq.nameserver.replication;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 复制模式枚举
 * @create : 2025-05-07 10:39
 **/

public enum ReplicationModeEnum {
    SINGLE("single", "单机"),
    MASTER_SLAVE("master_slave", "主从复制"),
    TRACE("trace", "链路复制"),

    ;

    final String mode;
    final String desc;

    ReplicationModeEnum(String mode, String desc) {
        this.mode = mode;
        this.desc = desc;
    }

    public String getMode() {
        return mode;
    }

    public String getDesc() {
        return desc;
    }

    public static ReplicationModeEnum of(String mode) {
        for (ReplicationModeEnum value : ReplicationModeEnum.values()) {
            if (value.mode.equals(mode)) {
                return value;
            }
        }
        return null;
    }
}
