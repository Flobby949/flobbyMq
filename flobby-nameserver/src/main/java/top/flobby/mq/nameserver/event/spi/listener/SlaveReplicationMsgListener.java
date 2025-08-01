package top.flobby.mq.nameserver.event.spi.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.event.Listener;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;
import top.flobby.mq.nameserver.event.model.SlaveReplicationMsgAckEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 从节点专属的数据同步监听器
 * @create : 2025-05-07 17:12
 **/

public class SlaveReplicationMsgListener implements Listener<ReplicationMsgEvent> {

    public static final Logger LOGGER = LoggerFactory.getLogger(SlaveReplicationMsgListener.class);

    @Override
    public void onReceive(ReplicationMsgEvent event) throws Exception {
        ServiceInstance serviceInstance = event.getServiceInstance();
        // 从节点接受主节点同步数据
        CommonCache.getServiceInstanceManager().put(serviceInstance);
        LOGGER.info("从节点接受主节点数据");
        String masterEventId = event.getMsgId();
        SlaveReplicationMsgAckEvent ackEvent = new SlaveReplicationMsgAckEvent();
        ackEvent.setMsgId(masterEventId);
        TcpMsg msg = new TcpMsg(NameServerEventCodeEnum.MASTER_REPLICATION_MSG.getCode(), ackEvent);
        event.getCtx().channel().writeAndFlush(msg);
    }
}
