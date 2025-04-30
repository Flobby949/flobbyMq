package top.flobby.mq.nameserver.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsgDecoder;
import top.flobby.mq.common.coder.TcpMsgEncoder;
import top.flobby.mq.nameserver.handler.TcpNettyServerHandler;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 基于netty启动nameserver服务
 * @create : 2025-04-30 10:13
 **/

public class NameServerStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NameServerStarter.class);

    private int port;

    public NameServerStarter(int port) {
        this.port = port;
    }

    public void startServer() throws InterruptedException {
        // 构建netty服务
        // 注入编解码器
        // 注入特定的handler
        // 启动netty服务

        // 处理网络io的accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 处理网络io中的read和write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                // 初始化编解码器，初始化handler服务
                channel.pipeline().addLast(new TcpMsgDecoder());
                channel.pipeline().addLast(new TcpMsgEncoder());
                channel.pipeline().addLast(new TcpNettyServerHandler());
            }
        });
        // 监听jvm的关闭，进行优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            LOGGER.info("nameserver关闭成功");
        }));
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        LOGGER.info("nameserver启动成功，监听端口：{}", port);
        channelFuture.channel().closeFuture().sync();
    }
}
