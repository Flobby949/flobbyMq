package top.flobby.mq.nameserver.cache;

import io.netty.channel.Channel;
import top.flobby.mq.common.dto.NodeAckDto;
import top.flobby.mq.common.dto.SlaveAckDto;
import top.flobby.mq.nameserver.config.NameServerProperties;
import top.flobby.mq.nameserver.replication.ReplicationTask;
import top.flobby.mq.nameserver.store.ReplicationChannelManager;
import top.flobby.mq.nameserver.store.ReplicationMsgQueueManager;
import top.flobby.mq.nameserver.store.ServiceInstanceManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-04-30 16:44
 **/

public class CommonCache {

    private static ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager();
    private static NameServerProperties nameServerProperties = new NameServerProperties();
    private static ReplicationChannelManager replicationChannelManager = new ReplicationChannelManager();
    private static ReplicationTask replicationTask;
    private static Channel connectNodeChannel = null;
    // 上一个节点的channel通道
    private static Channel prevNodeChannel = null;
    private static ReplicationMsgQueueManager replicationMsgQueueManager = new ReplicationMsgQueueManager();
    // 主从复制消息的ack队列，key-消息id，value-需要ack的次数
    private static Map<String, SlaveAckDto> slaveAckMap = new ConcurrentHashMap<>();
    // 链式复制消息的ack队列
    private static Map<String, NodeAckDto> nodeAckMap = new ConcurrentHashMap<>();

    public static Map<String, NodeAckDto> getNodeAckMap() {
        return nodeAckMap;
    }

    public static void setNodeAckMap(Map<String, NodeAckDto> nodeAckMap) {
        CommonCache.nodeAckMap = nodeAckMap;
    }

    public static Channel getPrevNodeChannel() {
        return prevNodeChannel;
    }

    public static void setPrevNodeChannel(Channel prevNodeChannel) {
        CommonCache.prevNodeChannel = prevNodeChannel;
    }

    public static Map<String, SlaveAckDto> getSlaveAckMap() {
        return slaveAckMap;
    }

    public static void setSlaveAckMap(Map<String, SlaveAckDto> slaveAckMap) {
        CommonCache.slaveAckMap = slaveAckMap;
    }

    public static ReplicationMsgQueueManager getReplicationMsgQueueManager() {
        return replicationMsgQueueManager;
    }

    public static void setReplicationMsgQueueManager(ReplicationMsgQueueManager replicationMsgQueueManager) {
        CommonCache.replicationMsgQueueManager = replicationMsgQueueManager;
    }

    public static ReplicationTask getReplicationTask() {
        return replicationTask;
    }

    public static void setReplicationTask(ReplicationTask replicationTask) {
        CommonCache.replicationTask = replicationTask;
    }

    public static Channel getConnectNodeChannel() {
        return connectNodeChannel;
    }

    public static void setConnectNodeChannel(Channel connectNodeChannel) {
        CommonCache.connectNodeChannel = connectNodeChannel;
    }

    public static ReplicationChannelManager getReplicationChannelManager() {
        return replicationChannelManager;
    }

    public static void setReplicationChannelManager(ReplicationChannelManager replicationChannelManager) {
        CommonCache.replicationChannelManager = replicationChannelManager;
    }

    public static NameServerProperties getNameServerProperties() {
        return nameServerProperties;
    }

    public static void setNameServerProperties(NameServerProperties nameServerProperties) {
        CommonCache.nameServerProperties = nameServerProperties;
    }

    public static ServiceInstanceManager getServiceInstanceManager() {
        return serviceInstanceManager;
    }

    public static void setServiceInstanceManager(ServiceInstanceManager serviceInstanceManager) {
        CommonCache.serviceInstanceManager = serviceInstanceManager;
    }
}
