package top.flobby.mq.nameserver.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.nameserver.event.model.HeartBeatEvent;
import top.flobby.mq.nameserver.event.model.RegistryEvent;
import top.flobby.mq.nameserver.event.model.UnRegistryEvent;

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

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        LOGGER.info("接收到消息：{}", JSON.toJSONString(tcpMsg));
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        switch (Objects.requireNonNull(NameServerEventCodeEnum.getByCode(code))) {
            case REGISTRY:
                // 注册事件
                RegistryEvent registerEvent = JSON.parseObject(body, RegistryEvent.class);
                break;
            case UN_REGISTRY:
                // 下线事件
                UnRegistryEvent unRegistryEvent = JSON.parseObject(body, UnRegistryEvent.class);
                break;
            case HEART_BEAT:
                // 心跳事件
                HeartBeatEvent heartBeatEvent = JSON.parseObject(body, HeartBeatEvent.class);
                break;
            default:
                break;
        }
    }
}
