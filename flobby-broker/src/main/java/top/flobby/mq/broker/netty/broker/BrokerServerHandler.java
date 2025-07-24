package top.flobby.mq.broker.netty.broker;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.MessageDto;
import top.flobby.mq.common.enums.BrokerEventCodeEnum;
import top.flobby.mq.common.event.EventBus;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-24 14:47
 **/

@ChannelHandler.Sharable
public class BrokerServerHandler extends SimpleChannelInboundHandler<TcpMsg> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServerHandler.class);

    private EventBus eventBus;

    public BrokerServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOGGER.info("Producer连接成功");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        if (code == BrokerEventCodeEnum.PUSH_MSG.getCode()) {
            MessageDto message = JSON.parseObject(body, MessageDto.class);
            LOGGER.info("收到推送消息：{}", message);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
