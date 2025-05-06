package top.flobby.mq.broker.netty.nameserver;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;

/**
 * @author flobby
 */

@ChannelHandler.Sharable
public class NameServerRespChannelHandler extends SimpleChannelInboundHandler<TcpMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg msg) throws Exception {
        System.out.println("resp:" + JSON.toJSONString(msg));
        if (msg.getCode() == NameServerResponseCodeEnum.REGISTRY_SUCCESS.getCode()) {
            // 注册成功，需要开启一个定时任务，定时上报心跳数据
            CommonCache.getHeartBeatTaskManager().startTask();
        } else if (msg.getCode() == NameServerResponseCodeEnum.ERROR_USER_OR_PASSWORD.getCode()) {
            // 验证失败
            throw new RuntimeException("error nameserver user or password");
        }
    }
}