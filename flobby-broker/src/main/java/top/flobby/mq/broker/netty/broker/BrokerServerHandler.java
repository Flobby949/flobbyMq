package top.flobby.mq.broker.netty.broker;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg object) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
