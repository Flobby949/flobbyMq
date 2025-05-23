package top.flobby.mq.nameserver.event.spi.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.HeartBeatEvent;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 心跳事件监听
 * @create : 2025-04-30 16:17
 **/

public class HeartBeatListener implements Listener<HeartBeatEvent>{
    public static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatListener.class);

    @Override
    public void onReceive(HeartBeatEvent event) throws IllegalAccessException {
        // 认证通过后，有心跳包
        ChannelHandlerContext ctx = event.getCtx();
        // 如果存在这个标识，证明之前认证过
        if (!ctx.channel().hasAttr(AttributeKey.valueOf("reqId"))) {
            // 认证失败
            TcpMsg errorMsg = new TcpMsg(NameServerResponseCodeEnum.ERROR_ACCESS);
            // 回写失败消息
            ctx.writeAndFlush(errorMsg);
            ctx.close();
            throw new IllegalAccessException(NameServerResponseCodeEnum.ERROR_ACCESS.getDesc());
        }
        // LOGGER.info("收到心跳包：{}", JSON.toJSONString(event));
        // 心跳，客户端固定间隔发送
        String brokerIdentifyStr = (String) ctx.channel().attr(AttributeKey.valueOf("reqId")).get();
        String[] brokerInfoArr = brokerIdentifyStr.split(":");
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setBrokerIp(brokerInfoArr[0]);
        serviceInstance.setBrokerPort(Integer.parseInt(brokerInfoArr[1]));
        serviceInstance.setLastHeartBeatTime(event.getTimestamp());
        CommonCache.getServiceInstanceManager().putIfExist(serviceInstance);
        // 同步
        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setServiceInstance(serviceInstance);
        replicationMsgEvent.setCtx(ctx);
        replicationMsgEvent.setTimestamp(System.currentTimeMillis());
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);
    }
}
