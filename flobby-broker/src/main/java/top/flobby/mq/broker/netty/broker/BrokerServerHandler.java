package top.flobby.mq.broker.netty.broker;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.broker.event.mode.ConsumeMsgEvent;
import top.flobby.mq.broker.event.mode.PushMsgEvent;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.ConsumeMsgReqDto;
import top.flobby.mq.common.dto.MessageDto;
import top.flobby.mq.common.enums.BrokerEventCodeEnum;
import top.flobby.mq.common.event.EventBus;
import top.flobby.mq.common.event.model.Event;

import java.net.InetSocketAddress;

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
        eventBus.init();
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
        Event event;
        if (code == BrokerEventCodeEnum.PUSH_MSG.getCode()) {
            MessageDto message = JSON.parseObject(body, MessageDto.class);
            LOGGER.info("收到推送消息：{}", message);
            PushMsgEvent pushMsgEvent = new PushMsgEvent();
            pushMsgEvent.setMessage(message);
            pushMsgEvent.setMsgId(message.getMsgId());
            event = pushMsgEvent;
        } else if (code == BrokerEventCodeEnum.CONSUME_MSG.getCode()) {
            ConsumeMsgReqDto reqDto = JSON.parseObject(body, ConsumeMsgReqDto.class);
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
            reqDto.setIp(inetSocketAddress.getHostString());
            reqDto.setPort(inetSocketAddress.getPort());
            LOGGER.info("收到消费消息：{}", reqDto);
            ConsumeMsgEvent consumeMsgEvent = new ConsumeMsgEvent();
            consumeMsgEvent.setReqDto(reqDto);
            consumeMsgEvent.setMsgId(reqDto.getMsgId());
            event = consumeMsgEvent;
        } else {
            return;
        }
        event.setCtx(channelHandlerContext);
        event.setTimestamp(System.currentTimeMillis());
        eventBus.publish(event);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
