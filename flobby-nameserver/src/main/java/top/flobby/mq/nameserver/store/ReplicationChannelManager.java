package top.flobby.mq.nameserver.store;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 复制通道管理
 * @create : 2025-05-07 14:36
 **/

public class ReplicationChannelManager {

    private static Map<String, ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<>();

    public Map<String, ChannelHandlerContext> getChannelHandlerContextMap() {
        return channelHandlerContextMap;
    }

    public void put(String reqId, ChannelHandlerContext ctx) {
        channelHandlerContextMap.put(reqId, ctx);
    }

    public ChannelHandlerContext get(String reqId) {
        return channelHandlerContextMap.get(reqId);
    }
}
