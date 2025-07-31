package top.flobby.mq.broker.netty.nameserver;

import com.alibaba.fastjson2.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.config.GlobalProperties;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.coder.TcpMsgDecoder;
import top.flobby.mq.common.coder.TcpMsgEncoder;
import top.flobby.mq.common.constant.TcpConstants;
import top.flobby.mq.common.dto.ServiceRegistryReqDto;
import top.flobby.mq.common.enums.BrokerRoleEnum;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.enums.RegistryTypeEnum;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

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
                ByteBuf delimiter = Unpooled.copiedBuffer(TcpConstants.DEFAULT_DECODE_CHAR.getBytes());
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024 * 8, delimiter));
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
        ServiceRegistryReqDto dto = new ServiceRegistryReqDto();
        try {
            dto.setIp(Inet4Address.getLocalHost().getHostAddress());
            GlobalProperties globalProperties = CommonCache.getGlobalProperties();
            dto.setPort(globalProperties.getBrokerPort());
            dto.setUser(globalProperties.getNameserverUser());
            dto.setPassword(globalProperties.getNameserverPassword());
            dto.setRegistryType(RegistryTypeEnum.BROKER.name());
            // 假设 broker是主从架构，(producer向主节点发送数据，consumer从从节点拉取数据)
            Map<String, Object> attrs = new HashMap<>();
            // TODO Broker的角色
            attrs.put("role", BrokerRoleEnum.SINGLE.name());
            dto.setAttrs(attrs);
            TcpMsg tcpMsg = new TcpMsg(NameServerEventCodeEnum.REGISTRY.getCode(), JSON.toJSONBytes(dto));
            channel.writeAndFlush(tcpMsg);
            LOGGER.info("发送注册事件");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
