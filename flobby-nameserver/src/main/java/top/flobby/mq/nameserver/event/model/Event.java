package top.flobby.mq.nameserver.event.model;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 事件抽象类
 * @create : 2025-04-30 11:20
 **/

public abstract class Event {

    private long timestamp;
    private ChannelHandlerContext ctx;
}
