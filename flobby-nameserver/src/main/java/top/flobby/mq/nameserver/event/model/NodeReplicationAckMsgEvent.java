package top.flobby.mq.nameserver.event.model;

import top.flobby.mq.common.event.model.Event;
import top.flobby.mq.nameserver.enums.ReplicationMsgTypeEnum;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 链式复制ack消息
 * @create : 2025-07-17 10:34
 **/

public class NodeReplicationAckMsgEvent extends Event {
    private String nodeIp;
    private Integer nodePort;
    private ReplicationMsgTypeEnum type;

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public Integer getNodePort() {
        return nodePort;
    }

    public void setNodePort(Integer nodePort) {
        this.nodePort = nodePort;
    }

    public ReplicationMsgTypeEnum getType() {
        return type;
    }

    public void setType(ReplicationMsgTypeEnum type) {
        this.type = type;
    }
}
