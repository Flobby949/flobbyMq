package top.flobby.mq.nameserver.event.spi.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 从节点专属的数据同步监听器
 * @create : 2025-05-07 17:12
 **/

public class SlaveReplicationMsgListener implements Listener<ReplicationMsgEvent>{

    public static final Logger LOGGER = LoggerFactory.getLogger(SlaveReplicationMsgListener.class);

    @Override
    public void onReceive(ReplicationMsgEvent event) throws IllegalAccessException {
        ServiceInstance serviceInstance = event.getServiceInstance();
        // 从节点接受主节点同步数据
        CommonCache.getServiceInstanceManager().put(serviceInstance);
        LOGGER.info("从节点接受主节点数据");
    }
}
