package top.flobby.mq.nameserver.replication;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.SlaveAckDto;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.enums.MasterSlaveReplicationTypeEnum;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;

import java.util.Map;
import java.util.UUID;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 主从同步专用的数据发送任务
 * @create : 2025-05-07 14:49
 **/

public class MasterReplicationMsgSendTask extends ReplicationTask{
    public static final Logger LOGGER = LoggerFactory.getLogger(MasterReplicationMsgSendTask.class);

    public MasterReplicationMsgSendTask(String taskName) {
        super(taskName);
    }

    @Override
    public void initTask() {
            try {
                while (true) {
                    ReplicationMsgEvent event = CommonCache.getReplicationMsgQueueManager().getReplicationQueue().take();
                    event.setMsgId(UUID.randomUUID().toString());
                    ChannelHandlerContext brokerChannel = event.getCtx();
                    int validSlaveChannelCount = CommonCache.getReplicationChannelManager().getActiveChannel().size();
                    /**
                     * 判断当前采用的是什么同步方案
                     * sync - 同步复制，发送同步数据给slave节点，slave节点返回ack信号，主节点收到信号后通知broker注册成功
                     * async - 异步复制，
                     * 半同步复制，类似同步复制，但是不需要等待所有slave的ack信号
                     */
                    MasterSlaveReplicationTypeEnum typeEnum = MasterSlaveReplicationTypeEnum.of(CommonCache.getNameServerProperties().getMasterSlaveReplicationProperties().getType());
                    if (typeEnum == MasterSlaveReplicationTypeEnum.SYNC) {
                        // 同步复制，需要等待全部从节点ack
                        this.inputMsgToAckMap(event, validSlaveChannelCount);
                        this.sendMsgToSlave(event);
                    } else if (typeEnum == MasterSlaveReplicationTypeEnum.ASYNC) {
                        // 异步复制，发送同步数据，同时返回注册成功信号给到broker
                        this.sendMsgToSlave(event);
                        brokerChannel.writeAndFlush(new TcpMsg(NameServerResponseCodeEnum.REGISTRY_SUCCESS));
                    } else if (typeEnum == MasterSlaveReplicationTypeEnum.HALF_SYNC) {
                        // 半同步复制，只需要等待过半数量ack
                        this.inputMsgToAckMap(event, validSlaveChannelCount / 2);
                        this.sendMsgToSlave(event);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("发送数据异常：{}", e.getMessage());
            }
    }

    /**
     * 将 msg 发送到 slave
     *
     * @param replicationMsgEvent 复制 msg 事件
     */
    private void sendMsgToSlave(ReplicationMsgEvent replicationMsgEvent) {
        LOGGER.info("主节点发送数据：{}", JSON.toJSONString(replicationMsgEvent));
        Map<String, ChannelHandlerContext> ctxMap =
                CommonCache.getReplicationChannelManager().getActiveChannel();
        for (String reqId : ctxMap.keySet()) {
            ChannelHandlerContext slaveChannel = ctxMap.get(reqId);
            replicationMsgEvent.setCtx(null);
            byte[] body = JSON.toJSONBytes(replicationMsgEvent);
            // 异步复制自己发送给从节点，并且告知broker注册成功
            TcpMsg msg = new TcpMsg(NameServerEventCodeEnum.MASTER_REPLICATION_MSG.getCode(), body);
            slaveChannel.writeAndFlush(msg);
        }
    }

    /**
     * 发送 msg 到 ackMap
     */
    private void inputMsgToAckMap(ReplicationMsgEvent replicationMsgEvent, int needAckTimes) {
        CommonCache.getSlaveAckMap().put(replicationMsgEvent.getMsgId(), new SlaveAckDto(needAckTimes, replicationMsgEvent.getCtx()));
    }
}
