package top.flobby.mq.nameserver.replication;

import io.netty.channel.Channel;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.SlaveHeartBeatEvent;
import top.flobby.mq.nameserver.event.model.StartReplicationEvent;

import java.util.concurrent.TimeUnit;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 从节点给主节点发送心跳的任务
 * @create : 2025-05-07 16:27
 **/

public class SlaveReplicationHeartBeatTask extends ReplicationTask {

    public SlaveReplicationHeartBeatTask(String taskName) {
        super(taskName);
    }

    @Override
    public void initTask() {
        StartReplicationEvent event = new StartReplicationEvent();
        event.setUser(CommonCache.getNameServerProperties().getNameserverUser());
        event.setPassword(CommonCache.getNameServerProperties().getNameserverPassword());
        TcpMsg startReplicationMsg = new TcpMsg(NameServerEventCodeEnum.START_REPLICATION.getCode(), event);
        CommonCache.getMasterConnection().writeAndFlush(startReplicationMsg);
        while (true) {
            try {
                // 这里等待了三秒，可以看作等待主节点收到开始同步的消息
                TimeUnit.SECONDS.sleep(3);
                Channel channel = CommonCache.getMasterConnection();
                TcpMsg tcpMsg = new TcpMsg(NameServerEventCodeEnum.SLAVE_HEART_BEAT.getCode(), new SlaveHeartBeatEvent());
                channel.writeAndFlush(tcpMsg);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
