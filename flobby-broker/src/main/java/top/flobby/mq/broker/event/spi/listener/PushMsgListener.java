package top.flobby.mq.broker.event.spi.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.event.mode.PushMsgEvent;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.MessageDto;
import top.flobby.mq.common.dto.SendMsgToBrokerRespDto;
import top.flobby.mq.common.enums.BrokerResponseCodeEnum;
import top.flobby.mq.common.enums.MessageSendWayEnum;
import top.flobby.mq.common.enums.SendMsgToBrokerRespStatusEnum;
import top.flobby.mq.common.event.Listener;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-24 17:11
 **/

public class PushMsgListener implements Listener<PushMsgEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushMsgListener.class);

    @Override
    public void onReceive(PushMsgEvent event) throws Exception {
        LOGGER.info("进入 PushMsg 处理器");
        MessageDto message = event.getMessage();
        // 消息写入commitLog
        CommonCache.getCommitLogAppendHandler().appendMsg(message);
        // 同步消息回应ack信号
        if (MessageSendWayEnum.SYNC.ordinal() == message.getSendWay()) {
            SendMsgToBrokerRespDto respDto = new SendMsgToBrokerRespDto();
            respDto.setStatus(SendMsgToBrokerRespStatusEnum.SUCCESS.ordinal());
            respDto.setMsgId(event.getMsgId());
            TcpMsg respMsg = new TcpMsg(BrokerResponseCodeEnum.SEND_MSG_RESP.getCode(), respDto);
            event.getCtx().writeAndFlush(respMsg);
        }
        // 消息写入consumeQueue
        // 回应ACK到服务端
    }
}
