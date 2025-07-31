package top.flobby.mq.broker.event.spi.listener;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.event.mode.ConsumeMsgEvent;
import top.flobby.mq.broker.model.ConsumeQueueConsumeReqModel;
import top.flobby.mq.broker.rebalance.ConsumerInstance;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.ConsumeMsgReqDto;
import top.flobby.mq.common.dto.ConsumeMsgRespDto;
import top.flobby.mq.common.enums.BrokerResponseCodeEnum;
import top.flobby.mq.common.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-30 16:35
 **/

public class ConsumeMsgListener implements Listener<ConsumeMsgEvent> {
    @Override
    public void onReceive(ConsumeMsgEvent event) throws Exception {
        ConsumeMsgReqDto msgReqDto = event.getReqDto();
        String topic = msgReqDto.getTopic();
        String consumeGroup = msgReqDto.getConsumeGroup();
        String currentReqId = msgReqDto.getIp() + ":" + msgReqDto.getPort();

        // 实例可能没有被添加过，方法做了重复添加的过滤，可以放心添加
        ConsumerInstance instance = new ConsumerInstance();
        instance.setIp(msgReqDto.getIp());
        instance.setPort(msgReqDto.getPort());
        instance.setConsumerReqId(currentReqId);
        instance.setTopic(topic);
        instance.setConsumeGroup(consumeGroup);
        // 获取消费队列的batchSize
        instance.setBatchSize(msgReqDto.getBatchSize());
        CommonCache.getConsumerInstancePool().addInstancePool(instance);

        // 根据topic和consumeGroup获取实例
        Map<String, List<ConsumerInstance>> consumeGroupMap = CommonCache.getConsumeHoldMap().getOrDefault(topic, new HashMap<>());
        // 有可能当前消费组还没有经过第一轮重平衡，因此还不能消费到消息
        List<ConsumerInstance> consumerInstanceList = consumeGroupMap.get(consumeGroup);
        if (consumerInstanceList == null || consumerInstanceList.isEmpty()) {
            return;
        }

        List<ConsumeMsgRespDto.ConsumeMsgRespItem> consumeRespItemList = new ArrayList<>();
        for (ConsumerInstance consumerInstance : consumerInstanceList) {
            if (consumerInstance.getConsumerReqId().equals(currentReqId)) {
                // 当前消费者有占有队列的权利 TODO
                for (Integer queueId : consumerInstance.getQueueIdSet()) {
                    ConsumeQueueConsumeReqModel req = new ConsumeQueueConsumeReqModel();
                    req.setTopic(topic);
                    req.setConsumeGroup(consumeGroup);
                    req.setQueueId(queueId);
                    req.setBatchSize(consumerInstance.getBatchSize());
                    List<byte[]> commitLogContentList = CommonCache.getConsumeQueueConsumeHandler().consume(req);
                    ConsumeMsgRespDto.ConsumeMsgRespItem item = new ConsumeMsgRespDto.ConsumeMsgRespItem();
                    item.setQueueId(queueId);
                    item.setCommitLogContentList(commitLogContentList);
                    consumeRespItemList.add(item);
                }
            }
        }
        ConsumeMsgRespDto respDto = new ConsumeMsgRespDto();
        respDto.setMsgId(event.getMsgId());
        respDto.setConsumeMsgRespItemList(consumeRespItemList);
        // 抓取数据结束，返回数据
        event.getCtx().writeAndFlush(new TcpMsg(
                BrokerResponseCodeEnum.CONSUME_MSG_RESP.getCode(),
                respDto
        ));
    }
}
