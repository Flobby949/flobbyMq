package top.flobby.mq.nameserver.store;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.config.TraceReplicationProperties;
import top.flobby.mq.nameserver.enums.ReplicationModeEnum;
import top.flobby.mq.nameserver.enums.ReplicationRoleEnum;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 同步消息队列
 * @create : 2025-05-07 17:20
 **/

public class ReplicationMsgQueueManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(ReplicationMsgQueueManager.class);

    private BlockingQueue<ReplicationMsgEvent> replicationQueue = new ArrayBlockingQueue<>(5000);

    public BlockingQueue<ReplicationMsgEvent> getReplicationQueue() {
        return replicationQueue;
    }

    public void put(ReplicationMsgEvent replicationMsgEvent) {
        ReplicationModeEnum modeEnum = ReplicationModeEnum.of(CommonCache.getNameServerProperties().getReplicationMode());
        if (modeEnum == null || modeEnum.equals(ReplicationModeEnum.SINGLE)) {
            return;
        }
        if (modeEnum.equals(ReplicationModeEnum.MASTER_SLAVE)) {
            ReplicationRoleEnum roleEnum =
                    ReplicationRoleEnum.of(CommonCache.getNameServerProperties().getMasterSlaveReplicationProperties().getRole());
            if (roleEnum.equals(ReplicationRoleEnum.SLAVE)) {
                return;
            }
            sendMsgToQueue(replicationMsgEvent);

        } else if (modeEnum.equals(ReplicationModeEnum.TRACE)) {
            TraceReplicationProperties traceReplicationProperties = CommonCache.getNameServerProperties().getTraceReplicationProperties();
            if (StringUtils.isNotBlank(traceReplicationProperties.getNextNode())) {
                // 如果有下一个节点（非尾节点），需要和下一个节点进行通信
                sendMsgToQueue(replicationMsgEvent);
            }
        }
    }


    private void sendMsgToQueue(ReplicationMsgEvent replicationMsgEvent) {
        try {
            // LOGGER.info("向阻塞队列中添加数据: {}", JSON.toJSONString(replicationMsgEvent));
            replicationQueue.put(replicationMsgEvent);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
