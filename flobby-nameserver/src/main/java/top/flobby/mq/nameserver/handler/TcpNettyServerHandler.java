package top.flobby.mq.nameserver.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.flobby.mq.common.coder.TcpMsg;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消息handler
 * @create : 2025-04-30 10:39
 **/

// 注解的作用是让这个handler变成单例
@ChannelHandler.Sharable
public class TcpNettyServerHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {

    }
}
