package top.flobby.mq.broker.event.spi.listener;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.event.mode.ConsumeMsgAckEvent;
import top.flobby.mq.broker.model.TopicModel;
import top.flobby.mq.broker.rebalance.ConsumerInstance;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.ConsumeMsgAckReqDto;
import top.flobby.mq.common.dto.ConsumeMsgAckRespDto;
import top.flobby.mq.common.enums.AckStatusEnum;
import top.flobby.mq.common.enums.BrokerResponseCodeEnum;
import top.flobby.mq.common.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消费端回应ack的监听
 * @create : 2025-07-31 15:01
 **/

public class ConsumeMsgAckListener implements Listener<ConsumeMsgAckEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumeMsgAckListener.class);

    @Override
    public void onReceive(ConsumeMsgAckEvent event) throws Exception {
        ConsumeMsgAckReqDto msgAck = event.getConsumeMsgAckReqDto();
        String topic = msgAck.getTopic();
        String consumeGroup = msgAck.getConsumeGroup();
        Integer queueId = msgAck.getQueueId();

        ConsumeMsgAckRespDto respDto = new ConsumeMsgAckRespDto();
        respDto.setMsgId(event.getMsgId());
        // topic 不存在
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topic);
        if (topicModel == null) {
            respDto.setAckStatus(AckStatusEnum.FAIL.ordinal());
            event.getCtx().writeAndFlush(
                    new TcpMsg(BrokerResponseCodeEnum.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(), respDto)
            );
            return;
        }
        Map<String, List<ConsumerInstance>> consumerInstanceMap = CommonCache.getConsumeHoldMap().getOrDefault(topic, new HashMap<>());
        List<ConsumerInstance> consumerInstanceList = consumerInstanceMap.get(consumeGroup);
        // 重平衡后获取不到当前消费实例
        if (CollectionUtils.isEmpty(consumerInstanceList)) {
            respDto.setAckStatus(AckStatusEnum.FAIL.ordinal());
            event.getCtx().writeAndFlush(
                    new TcpMsg(BrokerResponseCodeEnum.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(), respDto)
            );
            return;
        }
        // 查看当前消费实例是否拥有队列
        String currentReqId = msgAck.getIp() + ":" + msgAck.getPort();
        ConsumerInstance consumerInstance = consumerInstanceList.stream()
                .filter(item -> item.getConsumerReqId().equals(currentReqId))
                .findAny().orElse(null);
        if (consumerInstance == null) {
            respDto.setAckStatus(AckStatusEnum.FAIL.ordinal());
            event.getCtx().writeAndFlush(
                    new TcpMsg(BrokerResponseCodeEnum.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(), respDto)
            );
            return;
        }

        // 数据ACK，应该客户端传递offset还是服务端计算offset ?
        for (int i = 0; i < msgAck.getAckCount(); i++) {
            CommonCache.getConsumeQueueConsumeHandler().ack(topic, consumeGroup, queueId);
        }
        LOGGER.info("消费端ACK成功，topic: {}, consumeGroup: {}, queueId: {}", topic, consumeGroup, queueId);


        event.getCtx().writeAndFlush(
                new TcpMsg(BrokerResponseCodeEnum.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(), respDto)
        );
    }
}
