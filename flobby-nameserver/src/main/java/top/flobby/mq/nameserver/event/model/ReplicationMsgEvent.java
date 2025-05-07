package top.flobby.mq.nameserver.event.model;

import top.flobby.mq.nameserver.store.ServiceInstance;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 复制任务事件
 * @create : 2025-05-07 14:51
 **/

public class ReplicationMsgEvent extends Event{
    private ServiceInstance serviceInstance;

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
}
