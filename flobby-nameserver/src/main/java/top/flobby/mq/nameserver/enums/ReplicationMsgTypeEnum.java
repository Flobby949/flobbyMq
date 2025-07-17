package top.flobby.mq.nameserver.enums;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-17 17:29
 **/

public enum ReplicationMsgTypeEnum {
    REGISTRY(1,"节点复制"),
    HEART_BEAT(2,"心跳");

    ReplicationMsgTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    final int code;
    final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
