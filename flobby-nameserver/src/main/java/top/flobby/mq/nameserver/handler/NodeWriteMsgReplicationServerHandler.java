package top.flobby.mq.nameserver.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.EventBus;
import top.flobby.mq.nameserver.event.model.Event;
import top.flobby.mq.nameserver.event.model.NodeReplicationMsgEvent;

import java.util.Objects;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 当前节点接受外界写入数据handler
 * @create : 2025-05-21 10:31
 **/

@ChannelHandler.Sharable
public class NodeWriteMsgReplicationServerHandler extends SimpleChannelInboundHandler<TcpMsg> {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NodeWriteMsgReplicationServerHandler.class);

    private EventBus eventBus;
    public NodeWriteMsgReplicationServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        Event event;
        switch (Objects.requireNonNull(NameServerEventCodeEnum.getByCode(code))) {
            case NODE_REPLICATION_MSG:
                // 注册事件
                event = JSON.parseObject(body, NodeReplicationMsgEvent.class);
                break;
            default:
                event = new Event();
                break;
        }
        event.setCtx(channelHandlerContext);
        event.setTimestamp(System.currentTimeMillis());
        // 记录一下上一个节点的channel
        CommonCache.setPrevNodeChannel(channelHandlerContext.channel());
        eventBus.publish(event);
    }
}
