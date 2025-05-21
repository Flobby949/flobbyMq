package top.flobby.mq.nameserver.enums;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 复制模式枚举
 * @create : 2025-05-07 10:39
 **/

public enum ReplicationRoleEnum {
    MASTER("master", "主节点"),
    SLAVE("slave", "从节点"),

    TAIL("tail", "尾节点"),
    NODE("node", "普通节点"),

    ;

    final String role;
    final String desc;

    ReplicationRoleEnum(String mode, String desc) {
        this.role = mode;
        this.desc = desc;
    }

    public String getRole() {
        return role;
    }

    public String getDesc() {
        return desc;
    }

    public static ReplicationRoleEnum of(String mode) {
        for (ReplicationRoleEnum value : ReplicationRoleEnum.values()) {
            if (value.role.equals(mode)) {
                return value;
            }
        }
        return null;
    }
}
