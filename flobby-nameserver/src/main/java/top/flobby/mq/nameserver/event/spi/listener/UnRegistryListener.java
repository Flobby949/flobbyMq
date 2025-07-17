package top.flobby.mq.nameserver.event.spi.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final Logger LOGGER = LoggerFactory.getLogger(UnRegistryListener.class);

    @Override
    public void onReceive(UnRegistryEvent event) throws Exception {
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
        LOGGER.info("收到 {} 下线请求", brokerIdentifyStr);
        boolean removeStatus = CommonCache.getServiceInstanceManager().remove(brokerIdentifyStr);
        // 这个时候连接已经断开了，再回写消息意义不大
        // if (removeStatus) {
        //     TcpMsg respMsg = new TcpMsg(NameServerResponseCodeEnum.UN_REGISTRY_SERVICE);
        //     ctx.writeAndFlush(respMsg);
        //     ctx.close();
        // }
    }
}
