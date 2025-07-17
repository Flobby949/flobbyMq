package top.flobby.mq.nameserver.event.model;

import top.flobby.mq.nameserver.enums.ReplicationMsgTypeEnum;
import top.flobby.mq.nameserver.store.ServiceInstance;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-05-21 15:32
 **/

public class NodeReplicationMsgEvent extends Event{

    /**
     * @see top.flobby.mq.nameserver.enums.ReplicationMsgTypeEnum
     */
    private ReplicationMsgTypeEnum type;

    private ServiceInstance serviceInstance;

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public ReplicationMsgTypeEnum getType() {
        return type;
    }

    public void setType(ReplicationMsgTypeEnum type) {
        this.type = type;
    }
}
