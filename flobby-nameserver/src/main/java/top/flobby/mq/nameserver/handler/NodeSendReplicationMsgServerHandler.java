package top.flobby.mq.nameserver.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.event.EventBus;
import top.flobby.mq.common.event.model.Event;
import top.flobby.mq.nameserver.event.model.NodeReplicationAckMsgEvent;

import java.util.Objects;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 像下一个节点发送同步消息的handler，下一个节点返回的内容接收器
 * @create : 2025-05-21 10:32
 **/

@ChannelHandler.Sharable
public class NodeSendReplicationMsgServerHandler extends SimpleChannelInboundHandler<TcpMsg> {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NodeSendReplicationMsgServerHandler.class);

    private EventBus eventBus;
    public NodeSendReplicationMsgServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        Event event;
        if (Objects.requireNonNull(NameServerEventCodeEnum.getByCode(code)) == NameServerEventCodeEnum.NODE_REPLICATION_ACK_MSG) {
            // 注册事件
            event = JSON.parseObject(body, NodeReplicationAckMsgEvent.class);
        } else {
            event = new Event();
        }
        event.setCtx(channelHandlerContext);
        event.setTimestamp(System.currentTimeMillis());
        eventBus.publish(event);
    }
}
