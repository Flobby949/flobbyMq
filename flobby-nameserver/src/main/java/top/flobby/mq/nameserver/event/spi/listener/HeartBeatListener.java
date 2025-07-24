package top.flobby.mq.nameserver.event.spi.listener;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.HeartbeatDto;
import top.flobby.mq.common.dto.ServiceRegistryRespDto;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.enums.ReplicationModeEnum;
import top.flobby.mq.nameserver.enums.ReplicationMsgTypeEnum;
import top.flobby.mq.nameserver.event.model.HeartBeatEvent;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;

import java.util.UUID;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 心跳事件监听
 * @create : 2025-04-30 16:17
 **/

public class HeartBeatListener implements Listener<HeartBeatEvent>{
    public static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatListener.class);

    @Override
    public void onReceive(HeartBeatEvent event) throws Exception {
        // 认证通过后，有心跳包
        ChannelHandlerContext ctx = event.getCtx();
        // 如果存在这个标识，证明之前认证过
        if (!ctx.channel().hasAttr(AttributeKey.valueOf("reqId"))) {
            // 认证失败
            ServiceRegistryRespDto respDto = new ServiceRegistryRespDto();
            respDto.setMsgId(event.getMsgId());
            TcpMsg errorMsg = new TcpMsg(NameServerResponseCodeEnum.ERROR_ACCESS.getCode(), respDto);
            // 回写失败消息
            ctx.writeAndFlush(errorMsg);
            ctx.close();
            throw new IllegalAccessException(NameServerResponseCodeEnum.ERROR_ACCESS.getDesc());
        }
        LOGGER.info("收到心跳包：{}", JSON.toJSONString(event));
        // 心跳，客户端固定间隔发送
        String brokerIdentifyStr = (String) ctx.channel().attr(AttributeKey.valueOf("reqId")).get();
        String[] brokerInfoArr = brokerIdentifyStr.split(":");
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setIp(brokerInfoArr[0]);
        serviceInstance.setPort(Integer.parseInt(brokerInfoArr[1]));
        serviceInstance.setLastHeartBeatTime(event.getTimestamp());
        CommonCache.getServiceInstanceManager().freshHeartBeat(serviceInstance);
        ReplicationModeEnum modeEnum = ReplicationModeEnum.of(CommonCache.getNameServerProperties().getReplicationMode());
        if (modeEnum == null || modeEnum.equals(ReplicationModeEnum.SINGLE)) {
            // 单机架构
            HeartbeatDto heartbeatDto = new HeartbeatDto();
            heartbeatDto.setMsgId(event.getMsgId());
            TcpMsg heartbeatMsg = new TcpMsg(NameServerResponseCodeEnum.HEART_BEAT_SUCCESS.getCode(), heartbeatDto);
            ctx.writeAndFlush(heartbeatMsg);
            return;
        }
        // 同步
        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setServiceInstance(serviceInstance);
        replicationMsgEvent.setMsgId(UUID.randomUUID().toString());
        replicationMsgEvent.setType(ReplicationMsgTypeEnum.HEART_BEAT);
        replicationMsgEvent.setCtx(ctx);
        replicationMsgEvent.setTimestamp(System.currentTimeMillis());
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);
    }
}
