package top.flobby.mq.common.remote;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.flobby.mq.common.coder.TcpMsg;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : broker的响应handler
 * @create : 2025-07-24 15:36
 **/

@ChannelHandler.Sharable
public class BrokerRemoteRespHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {

    }
}
