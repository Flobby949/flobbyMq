package top.flobby.mq.nameserver.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.coder.TcpMsgDecoder;
import top.flobby.mq.common.coder.TcpMsgEncoder;
import top.flobby.mq.common.constant.NameServerConstants;
import top.flobby.mq.nameserver.handler.NameServerRespChannelHandler;

import java.util.concurrent.TimeUnit;

public class NameServerStarterTest {

    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String DEFAULT_NAMESERVER_IP = "127.0.0.1";

    @BeforeEach
    public void setUp() {
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(new NameServerRespChannelHandler());
            }
        });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(DEFAULT_NAMESERVER_IP, NameServerConstants.DEFAULT_NAMESERVER_PORT).sync();
            channel = channelFuture.channel();
            System.out.println("success connected to nameserver!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSendMsg() {
        for (int i = 0; i < 100; i++) {
            try {
                System.out.println("isActive:" + channel.isActive());
                TimeUnit.SECONDS.sleep(1);
                String msgBody = "this is client test string";
                TcpMsg tcpMsg = new TcpMsg(1, msgBody.getBytes());
                channel.writeAndFlush(tcpMsg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}