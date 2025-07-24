package top.flobby.mq.nameserver.event.spi.listener;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.NodeAckDto;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.common.event.Listener;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.enums.ReplicationMsgTypeEnum;
import top.flobby.mq.nameserver.event.model.NodeReplicationAckMsgEvent;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-17 10:46
 **/

public class NodeReplicationAckMsgListener implements Listener<NodeReplicationAckMsgEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeReplicationAckMsgListener.class);

    @Override
    public void onReceive(NodeReplicationAckMsgEvent event) throws Exception {
        Channel prevNodeChannel = CommonCache.getPrevNodeChannel();
        // 上一个节点不存在证明是头节点，如果是头节点，告知broker客户端，同步完成
        if (prevNodeChannel == null) {
            // 根据下游返回的msgId，匹配发送端的msgId，找到broker的channel通道
            NodeAckDto nodeAckDto = CommonCache.getNodeAckMap().get(event.getMsgId());
            LOGGER.info("收到下游返回的ack：{} -> {}", event.getMsgId(), JSON.toJSONString(nodeAckDto));
            Channel brokerChannel = nodeAckDto.getCtx().channel();
            if (brokerChannel == null || !brokerChannel.isActive()) {
                throw new RuntimeException("broker connection is broken！");
            }
            CommonCache.getNodeAckMap().remove(event.getMsgId());
            // 回写给broker，告知成功
            if (event.getType().equals(ReplicationMsgTypeEnum.REGISTRY)) {
                brokerChannel.writeAndFlush(new TcpMsg(
                        NameServerResponseCodeEnum.REGISTRY_SUCCESS
                ));
            } else if (event.getType().equals(ReplicationMsgTypeEnum.HEART_BEAT)) {
                brokerChannel.writeAndFlush(new TcpMsg(
                        NameServerResponseCodeEnum.HEART_BEAT_SUCCESS
                ));
            }
            return;
        }
        // 判断当前节点是否是中间节点，继续告知上一个节点
        LOGGER.info("向上游返回ack：{}", JSON.toJSONString(event));
        prevNodeChannel.writeAndFlush(
                new TcpMsg(
                        NameServerEventCodeEnum.NODE_REPLICATION_ACK_MSG.getCode(), JSON.toJSONBytes(event)
                )
        );
    }
}
