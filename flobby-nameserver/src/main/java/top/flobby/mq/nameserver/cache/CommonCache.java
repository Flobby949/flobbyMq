package top.flobby.mq.nameserver.cache;

import top.flobby.mq.nameserver.config.NameServerProperties;
import top.flobby.mq.nameserver.store.ReplicationChannelManager;
import top.flobby.mq.nameserver.store.ServiceInstanceManager;

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
