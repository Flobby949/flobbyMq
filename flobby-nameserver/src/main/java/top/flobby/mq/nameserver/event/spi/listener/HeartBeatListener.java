package top.flobby.mq.nameserver.event.spi.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.HeartBeatEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 心跳事件监听
 * @create : 2025-04-30 16:17
 **/

public class HeartBeatListener implements Listener<HeartBeatEvent>{

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
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setBrokerIp(event.getBrokerIp());
        serviceInstance.setBrokerPort(event.getBrokerPort());
        serviceInstance.setLastHeartBeatTime(System.currentTimeMillis());
        CommonCache.getServiceInstanceManager().putIfExist(serviceInstance);
    }
}
