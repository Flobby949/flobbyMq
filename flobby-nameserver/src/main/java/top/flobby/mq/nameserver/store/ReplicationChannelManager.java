package top.flobby.mq.nameserver.store;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * 获取有效通道
     */
    public Map<String, ChannelHandlerContext> getActiveChannel() {
        List<String> inValidChannelReqIdList = new ArrayList<>();

        for (String reqId : channelHandlerContextMap.keySet()) {
            ChannelHandlerContext slaveChannel = channelHandlerContextMap.get(reqId);
            if (!slaveChannel.channel().isActive()) {
                inValidChannelReqIdList.add(reqId);
            }
        }

        // 移除不可用的channel
        if (!inValidChannelReqIdList.isEmpty()) {
            for (String reqId : inValidChannelReqIdList) {
                channelHandlerContextMap.remove(reqId);
            }
        }

        return channelHandlerContextMap;
    }

    public void put(String reqId, ChannelHandlerContext ctx) {
        channelHandlerContextMap.put(reqId, ctx);
    }

    public ChannelHandlerContext get(String reqId) {
        return channelHandlerContextMap.get(reqId);
    }
}
