package top.flobby.mq.common.dto;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 链式复制ack消息
 * @create : 2025-07-17 10:55
 **/

public class NodeAckDto {
    private ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
