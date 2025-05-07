package top.flobby.mq.nameserver.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.nameserver.event.EventBus;
import top.flobby.mq.nameserver.event.model.Event;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 主从复制服务handler
 * @create : 2025-05-07 10:58
 **/

@ChannelHandler.Sharable
public class SlaveReplicationServerHandler extends SimpleChannelInboundHandler<TcpMsg> {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SlaveReplicationServerHandler.class);

    private EventBus eventBus;
    public SlaveReplicationServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        Event event = new Event();
        // 从节点发起连接，在master端验证密码，建立连接
        if (code == NameServerEventCodeEnum.MASTER_REPLICATION_MSG.getCode()) {
            event = JSON.parseObject(body, ReplicationMsgEvent.class);
        }

        // 连接建立完成后，master收到的数据，同步发送给slave节点

        event.setCtx(channelHandlerContext);
        event.setTimestamp(System.currentTimeMillis());
        eventBus.publish(event);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        LOGGER.error("Error processing message: {}", cause.getMessage(), cause);
    }
}
