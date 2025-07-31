package top.flobby.mq.broker.netty.broker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsgDecoder;
import top.flobby.mq.common.coder.TcpMsgEncoder;
import top.flobby.mq.common.constant.TcpConstants;
import top.flobby.mq.common.event.EventBus;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : broker服务,接收producer的消息
 * @create : 2025-07-24 14:45
 **/

public class BrokerServerStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServerStarter.class);

    private int port;

    public BrokerServerStarter(int port) {
        this.port = port;
    }

    public void startServer() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                // 防止消息粘包导致消息流断开
                ByteBuf delimiter = Unpooled.copiedBuffer(TcpConstants.DEFAULT_DECODE_CHAR.getBytes());
                channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024 * 8, delimiter));
                // 初始化编解码器，初始化handler服务
                channel.pipeline().addLast(new TcpMsgDecoder());
                channel.pipeline().addLast(new TcpMsgEncoder());
                channel.pipeline().addLast(new BrokerServerHandler(new EventBus("broker-connection")));
            }
        });
        // 监听jvm的关闭，进行优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            LOGGER.info("broker服务关闭成功");
        }));
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        LOGGER.info("Broker服务启动成功，监听端口：{}", port);
        channelFuture.channel().closeFuture().sync();
    }
}
