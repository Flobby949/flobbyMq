package top.flobby.mq.nameserver.replication;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final Logger LOGGER = LoggerFactory.getLogger(SlaveReplicationHeartBeatTask.class);

    public SlaveReplicationHeartBeatTask(String taskName) {
        super(taskName);
    }

    @Override
    public void initTask() {
        try {
            // 为了确保主节点启动，先休眠一段时间
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StartReplicationEvent event = new StartReplicationEvent();
        event.setUser(CommonCache.getNameServerProperties().getNameserverUser());
        event.setPassword(CommonCache.getNameServerProperties().getNameserverPassword());
        TcpMsg startReplicationMsg = new TcpMsg(NameServerEventCodeEnum.START_REPLICATION.getCode(), event);
        CommonCache.getConnectNodeChannel().writeAndFlush(startReplicationMsg);
        LOGGER.info("从节点向主节点发送 START_REPLICATION 消息");
        while (true) {
            try {
                // 这里等待了三秒，可以看作等待主节点收到开始同步的消息
                TimeUnit.SECONDS.sleep(3);
                Channel channel = CommonCache.getConnectNodeChannel();
                TcpMsg tcpMsg = new TcpMsg(NameServerEventCodeEnum.SLAVE_HEART_BEAT.getCode(), new SlaveHeartBeatEvent());
                channel.writeAndFlush(tcpMsg);
                // LOGGER.info("从节点向主节点发送心跳数据");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
