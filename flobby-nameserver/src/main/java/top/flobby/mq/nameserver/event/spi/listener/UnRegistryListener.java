package top.flobby.mq.nameserver.event.spi.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.UnRegistryEvent;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 下线事件监听
 * @create : 2025-04-30 16:17
 **/

public class UnRegistryListener implements Listener<UnRegistryEvent>{

    @Override
    public void onReceive(UnRegistryEvent event) throws IllegalAccessException {
        ChannelHandlerContext ctx = event.getCtx();
        // 先校验是否有权限
        if (!ctx.channel().hasAttr(AttributeKey.valueOf("reqId")))  {
            // 认证失败
            TcpMsg errorMsg = new TcpMsg(NameServerResponseCodeEnum.ERROR_ACCESS);
            // 回写失败消息
            ctx.writeAndFlush(errorMsg);
            ctx.close();
            throw new IllegalAccessException(NameServerResponseCodeEnum.ERROR_ACCESS.getDesc());
        }
        String brokerIdentifyStr = (String) ctx.channel().attr(AttributeKey.valueOf("reqId")).get();
        boolean removeStatus = CommonCache.getServiceInstanceManager().remove(brokerIdentifyStr);
        if (removeStatus) {
            TcpMsg respMsg = new TcpMsg(NameServerResponseCodeEnum.UN_REGISTRY_SERVICE);
            ctx.writeAndFlush(respMsg);
            ctx.close();
        }
    }
}
