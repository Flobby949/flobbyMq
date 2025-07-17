package top.flobby.mq.broker.netty.nameserver;

import com.alibaba.fastjson2.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.config.GlobalProperties;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.coder.TcpMsgDecoder;
import top.flobby.mq.common.coder.TcpMsgEncoder;
import top.flobby.mq.common.dto.RegistryDto;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;

import java.net.Inet4Address;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 负责与nameserver服务端创建长连接
 * @create : 2025-05-06 09:39
 **/

public class NameServerClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(NameServerClient.class);

    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;

    /**
     * 初始化连接
     */
    public void initConnect() {
        String nameserverIp = CommonCache.getGlobalProperties().getNameserverIp();
        Integer nameserverPort = CommonCache.getGlobalProperties().getNameserverPort();
        if (StringUtils.isBlank(nameserverIp) || nameserverPort == null || nameserverPort <= 0) {
            throw new RuntimeException("nameserver ip or port is null");
        }
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
        // 监听jvm的关闭，进行优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clientGroup.shutdownGracefully();
            LOGGER.info("nameserver client 关闭成功");
        }));
        try {
            channelFuture = bootstrap.connect(nameserverIp, nameserverPort).sync();
            channel = channelFuture.channel();
            LOGGER.info("success connected to nameserver!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取channel通道
     *
     * @return {@link Channel }
     */
    public Channel getChannel() {
        if (channel == null || !channel.isActive()) {
            throw new RuntimeException("channel has not been connected!");
        }
        return channel;
    }

    /**
     * 发送注册消息
     */
    public void sendRegistryMsg() {
        RegistryDto dto = new RegistryDto();
        try {
            dto.setBrokerIp(Inet4Address.getLocalHost().getHostAddress());
            GlobalProperties globalProperties = CommonCache.getGlobalProperties();
            dto.setBrokerPort(globalProperties.getBrokerPort());
            dto.setUser(globalProperties.getNameserverUser());
            dto.setPassword(globalProperties.getNameserverPassword());
            TcpMsg tcpMsg = new TcpMsg(NameServerEventCodeEnum.REGISTRY.getCode(), JSON.toJSONBytes(dto));
            channel.writeAndFlush(tcpMsg);
            LOGGER.info("发送注册事件");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
