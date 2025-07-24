package top.flobby.mq.nameserver.event.spi.listener;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.event.Listener;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.config.TraceReplicationProperties;
import top.flobby.mq.nameserver.event.model.NodeReplicationAckMsgEvent;
import top.flobby.mq.nameserver.event.model.NodeReplicationMsgEvent;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;

import java.net.Inet4Address;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 接受上一个节点同步过来的数据复制内容
 * @create : 2025-07-17 10:17
 **/

public class NodeReplicationMsgListener implements Listener<NodeReplicationMsgEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeReplicationMsgListener.class);

    @Override
    public void onReceive(NodeReplicationMsgEvent event) throws Exception {
        ServiceInstance serviceInstance = event.getServiceInstance();
        // 接收到上一个节点同步过来的数据，存入本地内存
        CommonCache.getServiceInstanceManager().put(serviceInstance);
        // 普通节点，向队列中推，继续传递给下一个节点
        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setServiceInstance(serviceInstance);
        replicationMsgEvent.setMsgId(event.getMsgId());
        replicationMsgEvent.setType(event.getType());
        LOGGER.info("接收到上一个节点写入的数据：{}", JSON.toJSONString(event));
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);
        // TODO 如果是尾节点，不需要给下一个节点复制，但是需要ack给上一个节点
        TraceReplicationProperties traceReplicationProperties = CommonCache.getNameServerProperties().getTraceReplicationProperties();
        String nextNode = traceReplicationProperties.getNextNode();
        if (StringUtils.isBlank(nextNode)) {
            LOGGER.info("当前是尾节点，返回ack给上一个节点");
            NodeReplicationAckMsgEvent ackMsgEvent = new NodeReplicationAckMsgEvent();
            ackMsgEvent.setNodeIp(Inet4Address.getLocalHost().getHostAddress());
            ackMsgEvent.setNodePort(traceReplicationProperties.getPort());
            ackMsgEvent.setMsgId(event.getMsgId());
            ackMsgEvent.setType(event.getType());
            Channel prevNodeChannel = CommonCache.getPrevNodeChannel();
            prevNodeChannel.writeAndFlush(new TcpMsg(NameServerEventCodeEnum.NODE_REPLICATION_ACK_MSG.getCode(), JSON.toJSONBytes(ackMsgEvent)));
        }

    }
}
