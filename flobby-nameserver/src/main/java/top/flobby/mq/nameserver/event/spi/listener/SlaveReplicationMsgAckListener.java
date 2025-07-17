package top.flobby.mq.nameserver.event.spi.listener;

import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.SlaveAckDto;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.common.utils.AssertUtil;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.SlaveReplicationMsgAckEvent;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 从节点ack消息监听器
 * @create : 2025-05-08 10:55
 **/

public class SlaveReplicationMsgAckListener implements Listener<SlaveReplicationMsgAckEvent>{
    @Override
    public void onReceive(SlaveReplicationMsgAckEvent event) throws Exception {
        String masterReplicationMsgId = event.getMsgId();
        SlaveAckDto slaveAckDto = CommonCache.getSlaveAckMap().get(masterReplicationMsgId);
        AssertUtil.isNotNull(slaveAckDto, "error slave ack msg id: " + masterReplicationMsgId);
        int currentAckTime = slaveAckDto.getNeedAckTimes().decrementAndGet();
        // 如果是同步复制模式，代表所有从节点已经ack了
        // 如果是半同步复制模式，代表已经过半
        if (currentAckTime == 0) {
            CommonCache.getSlaveAckMap().remove(masterReplicationMsgId);
            slaveAckDto.getBrokerChannel().writeAndFlush(new TcpMsg(NameServerResponseCodeEnum.REGISTRY_SUCCESS));
        }
    }
}
