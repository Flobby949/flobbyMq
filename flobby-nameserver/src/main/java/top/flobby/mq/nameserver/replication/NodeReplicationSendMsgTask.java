package top.flobby.mq.nameserver.replication;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.NodeAckDto;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.NodeReplicationMsgEvent;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 链式复制中，非尾节点发送数据给下一个节点
 * @create : 2025-05-21 15:05
 **/

public class NodeReplicationSendMsgTask extends ReplicationTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeReplicationSendMsgTask.class);

    public NodeReplicationSendMsgTask(String taskName) {
        super(taskName);
    }

    @Override
    void initTask() {
        while (true) {
            try {
                // 只要不是尾节点，就取出来，并且发送消息
                ReplicationMsgEvent event = CommonCache.getReplicationMsgQueueManager().getReplicationQueue().take();
                Channel connectNodeChannel = CommonCache.getConnectNodeChannel();
                // 发送消息之前重新封装一个对象（偷懒，这样就不需要封装两个ReplicationMsgQueue，都从一个队列中取数据）
                NodeReplicationMsgEvent nodeReplicationMsgEvent = new NodeReplicationMsgEvent();
                nodeReplicationMsgEvent.setMsgId(event.getMsgId());
                nodeReplicationMsgEvent.setType(event.getType());
                nodeReplicationMsgEvent.setServiceInstance(event.getServiceInstance());
                if (connectNodeChannel != null && connectNodeChannel.isActive()) {
                    TcpMsg nextNodeMsg = new TcpMsg(NameServerEventCodeEnum.NODE_REPLICATION_MSG.getCode(), JSON.toJSONBytes(nodeReplicationMsgEvent));
                    connectNodeChannel.writeAndFlush(nextNodeMsg);
                }
                // 加入到ackMap中
                NodeAckDto ackDto = new NodeAckDto();
                ackDto.setCtx(event.getCtx());
                CommonCache.getNodeAckMap().put(event.getMsgId(), ackDto);
                LOGGER.warn("ack队列：{}, 加入 {}", CommonCache.getNodeAckMap().size(), event.getMsgId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
