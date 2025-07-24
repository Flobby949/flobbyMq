package top.flobby.mq.nameserver.event.spi.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.common.event.Listener;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.StartReplicationEvent;
import top.flobby.mq.nameserver.utils.NameServerUtil;

import java.net.InetSocketAddress;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 开启同步复制监听器
 * @create : 2025-05-07 14:32
 **/

public class StartReplicationListener implements Listener<StartReplicationEvent> {
    @Override
    public void onReceive(StartReplicationEvent event) throws Exception {
        boolean isVerify = NameServerUtil.checkUserAndPassword(event.getUser(), event.getPassword());
        ChannelHandlerContext ctx = event.getCtx();
        if (!isVerify) {
            // 注册失败
            TcpMsg errorMsg = new TcpMsg(NameServerResponseCodeEnum.ERROR_ACCESS);
            // 回写失败消息
            ctx.writeAndFlush(errorMsg);
            ctx.close();
            throw new IllegalAccessException(NameServerResponseCodeEnum.ERROR_ACCESS.getDesc());
        }
        // 向内存中加入
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        event.setSlaveIp(inetSocketAddress.getHostString());
        event.setSlavePort(inetSocketAddress.getPort());
        String reqId = event.getSlaveIp() + ":" + event.getSlavePort();
        ctx.channel().attr(AttributeKey.valueOf("reqId")).set(reqId);
        CommonCache.getReplicationChannelManager().put(reqId, ctx);
        TcpMsg tcpMsg = new TcpMsg(NameServerEventCodeEnum.MASTER_START_REPLICATION_ACK);
        ctx.writeAndFlush(tcpMsg);
    }
}
