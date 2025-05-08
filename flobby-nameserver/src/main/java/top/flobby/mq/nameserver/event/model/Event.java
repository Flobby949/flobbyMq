package top.flobby.mq.nameserver.event.model;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 事件抽象类
 * @create : 2025-04-30 11:20
 **/

public class Event {

    private String msgId;

    private long timestamp;
    /**
     * 方便使用事件关联的上下文的回写机制
     */
    private ChannelHandlerContext ctx;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
