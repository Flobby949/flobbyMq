package top.flobby.mq.broker.netty.nameserver;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;

/**
 * @author flobby
 */

@ChannelHandler.Sharable
public class NameServerRespChannelHandler extends SimpleChannelInboundHandler<TcpMsg> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NameServerRespChannelHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg msg) throws Exception {
        // LOGGER.info("resp: {}", JSON.toJSONString(msg));
        if (msg.getCode() == NameServerResponseCodeEnum.REGISTRY_SUCCESS.getCode()) {
            // 注册成功，需要开启一个定时任务，定时上报心跳数据
            LOGGER.info("nameServer 注册成功，开启心跳任务");
            CommonCache.getHeartBeatTaskManager().startTask();
        } else if (msg.getCode() == NameServerResponseCodeEnum.ERROR_USER_OR_PASSWORD.getCode()) {
            // 验证失败
            throw new RuntimeException("error nameserver user or password");
        } else if (msg.getCode() == NameServerResponseCodeEnum.HEART_BEAT_SUCCESS.getCode()) {
            // 心跳正常
            LOGGER.info("nameServer 心跳正常");
        }
    }
}