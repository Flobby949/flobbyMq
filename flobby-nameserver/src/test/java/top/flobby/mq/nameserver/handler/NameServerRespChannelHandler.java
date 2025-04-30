package top.flobby.mq.nameserver.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.flobby.mq.common.coder.TcpMsg;

@ChannelHandler.Sharable
public class NameServerRespChannelHandler extends SimpleChannelInboundHandler<TcpMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg msg) throws Exception {
        System.out.println("resp:" + JSON.toJSONString(msg));
    }
}