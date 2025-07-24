package top.flobby.mq.nameserver.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.HeartbeatDto;
import top.flobby.mq.common.dto.PullBrokerIpReqDto;
import top.flobby.mq.common.dto.ServiceRegistryReqDto;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.event.model.Event;
import top.flobby.mq.common.event.EventBus;
import top.flobby.mq.nameserver.event.model.*;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消息handler
 * @create : 2025-04-30 10:39
 **/

// 注解的作用是让这个handler变成单例
@ChannelHandler.Sharable
public class TcpNettyServerHandler extends SimpleChannelInboundHandler<TcpMsg> {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TcpNettyServerHandler.class);

    private EventBus eventBus;
    public TcpNettyServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        int code = tcpMsg.getCode();
        LOGGER.info("接收到消息: {}", tcpMsg);
        byte[] body = tcpMsg.getBody();
        Event event;
        switch (Objects.requireNonNull(NameServerEventCodeEnum.getByCode(code))) {
            case REGISTRY:
                // 注册事件
                ServiceRegistryReqDto reqDto = JSON.parseObject(body, ServiceRegistryReqDto.class);
                RegistryEvent registryEvent = new RegistryEvent();
                registryEvent.setMsgId(reqDto.getMsgId());
                registryEvent.setUser(reqDto.getUser());
                registryEvent.setPassword(reqDto.getPassword());
                registryEvent.setRegistryType(reqDto.getRegistryType());
                registryEvent.setAttrs(reqDto.getAttrs());
                if (StringUtils.isBlank(reqDto.getIp())) {
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
                    registryEvent.setIp(inetSocketAddress.getHostString());
                    registryEvent.setPort(inetSocketAddress.getPort());
                } else {
                    registryEvent.setIp(reqDto.getIp());
                    registryEvent.setPort(reqDto.getPort());
                }
                event = registryEvent;
                break;
            case UN_REGISTRY:
                // 下线事件
                event = JSON.parseObject(body, UnRegistryEvent.class);
                break;
            case HEART_BEAT:
                // 心跳事件
                HeartbeatDto heartbeatDto = JSON.parseObject(body, HeartbeatDto.class);
                HeartBeatEvent heartBeatEvent = new HeartBeatEvent();
                heartBeatEvent.setMsgId(heartbeatDto.getMsgId());
                event = heartBeatEvent;
                break;
            case PULL_BROKER_MASTER_IP:
                PullBrokerIpReqDto pullBrokerIpReqDto = JSON.parseObject(body, PullBrokerIpReqDto.class);
                PullBrokerIpEvent pullBrokerIpEvent = new PullBrokerIpEvent();
                pullBrokerIpEvent.setMsgId(pullBrokerIpReqDto.getMsgId());
                pullBrokerIpEvent.setRole(pullBrokerIpReqDto.getRole());
                event = pullBrokerIpEvent;
                break;
            default:
                event = new Event();
                break;
        }
        event.setCtx(channelHandlerContext);
        event.setTimestamp(System.currentTimeMillis());
        eventBus.publish(event);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // 只依赖定时任务剔除服务连接，在一些高要求的场景下三个心跳周期太长了，在这里监听并主动断开
        LOGGER.info("连接断开: {}", ctx.channel().remoteAddress());
        UnRegistryEvent unRegistryEvent = new UnRegistryEvent();
        unRegistryEvent.setCtx(ctx);
        unRegistryEvent.setTimestamp(System.currentTimeMillis());
        eventBus.publish(unRegistryEvent);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        LOGGER.error("Error processing message: {}", cause.getMessage(), cause);
    }
}
