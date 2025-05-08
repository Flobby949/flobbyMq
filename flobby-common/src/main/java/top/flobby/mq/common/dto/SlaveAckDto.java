package top.flobby.mq.common.dto;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 从节点ack消息
 * @create : 2025-05-08 11:06
 **/

public class SlaveAckDto {
    /**
     * 需要ack的次数
     */
    private AtomicInteger needAckTimes;

    /**
     * broker 连接主节点的 channel
     */
    private ChannelHandlerContext brokerChannel;

    public SlaveAckDto(Integer needAckTimes, ChannelHandlerContext brokerChannel) {
        this.needAckTimes = new AtomicInteger(needAckTimes);
        this.brokerChannel = brokerChannel;
    }

    public SlaveAckDto(AtomicInteger needAckTimes, ChannelHandlerContext brokerChannel) {
        this.needAckTimes = needAckTimes;
        this.brokerChannel = brokerChannel;
    }

    public AtomicInteger getNeedAckTimes() {
        return needAckTimes;
    }

    public void setNeedAckTimes(AtomicInteger needAckTimes) {
        this.needAckTimes = needAckTimes;
    }

    public ChannelHandlerContext getBrokerChannel() {
        return brokerChannel;
    }

    public void setBrokerChannel(ChannelHandlerContext brokerChannel) {
        this.brokerChannel = brokerChannel;
    }
}
