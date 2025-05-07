package top.flobby.mq.nameserver.store;

import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;
import top.flobby.mq.nameserver.replication.ReplicationModeEnum;
import top.flobby.mq.nameserver.replication.ReplicationRoleEnum;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 同步消息队列
 * @create : 2025-05-07 17:20
 **/

public class ReplicationMsgQueueManager {

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
            try {
                System.out.println("向阻塞队列中添加数据");
                replicationQueue.put(replicationMsgEvent);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
