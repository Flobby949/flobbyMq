package top.flobby.mq.broker.core;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 消费队列，消费处理器
 * @create : 2024-07-15 11:26
 **/

public class ConsumeQueueConsumeHandler {

    /**
     * 读取最新N条ConsumerQueue消息内容，并且返回commitLog原始内容
     *
     * @return {@link byte[] }
     */
    public  List<byte[]>  consume(ConsumeQueueConsumeReqModel reqModel) {
        // 检查参数合法性
        // 获取当前匹配的消费队列的最新的consumeQueue的offset
        // 获取当前匹配的队列存储文件的mmap对象，读取offset地址数据
        String topic = reqModel.getTopic();
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topic);
        if (topicModel == null) {
            throw new IllegalArgumentException("topic 【" + topic + "】is null");
        }
        String consumeGroup = reqModel.getConsumeGroup();
        Integer queueId = reqModel.getQueueId();
        // 一次拉取多少条消息
        Integer msgCount = reqModel.getBatchSize();
        ConsumeQueueOffsetModel.OffsetTable offsetTable = CommonCache.getConsumerQueueOffsetModel().getOffsetTable();
        Map<String, ConsumeQueueOffsetModel.ConsumerGroupDetail> topicConsumerGroupDetailMap = offsetTable.getTopicConsumerGroupDetail();
        ConsumeQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = topicConsumerGroupDetailMap.get(topic);
        // 如果是首次消费，初始化
        if (consumerGroupDetail == null) {
            consumerGroupDetail = new ConsumeQueueOffsetModel.ConsumerGroupDetail();
            topicConsumerGroupDetailMap.put(topic, consumerGroupDetail);
        }
        Map<String, Map<String, String>> consumeGroupOffsetMap = consumerGroupDetail.getConsumerGroupDetailMap();
        Map<String, String> queueOffsetDetail = consumeGroupOffsetMap.get(consumeGroup);
        // 如果detail不存在，那就初始化
        List<QueueModel> queueList = topicModel.getQueueList();
        if (queueOffsetDetail == null || queueOffsetDetail.isEmpty()) {
            queueOffsetDetail = new HashMap<>();
            // 每一个topic的队列
            for (QueueModel queueModel : queueList) {
                // 初始化时默认塞入0号文件
                queueOffsetDetail.put(String.valueOf(queueModel.getId()), "00000000#0");
            }
            consumeGroupOffsetMap.put(consumeGroup, queueOffsetDetail);
        }
        // 取出当前queue的offset信息，格式：00000000#0
        String offsetStrInfo = queueOffsetDetail.get(String.valueOf(queueId));
        String[] offsetArr = offsetStrInfo.split("#");
        // String consumeQueueFileName = offsetArr[0];
        Integer consumeQueueOffset = Integer.parseInt(offsetArr[1]);
        // 如果消费到了尽头，当前queue的消息offset小于了当前消费queue的消费offset，返回null
        QueueModel queueModel = queueList.get(queueId);
        if (queueModel.getLatestOffset().get() <= consumeQueueOffset) {
            return null;
        }
        // 通过queue映射的mmap，获取到具体的数据
        List<ConsumeQueueMMapFileModel> consumeQueueMMapFileModelList = CommonCache.getConsumeQueueMMapFileModelManager().get(topic);
        ConsumeQueueMMapFileModel consumeQueueMMapFileModel = consumeQueueMMapFileModelList.get(queueId);
        // 批量读取消息
        List<byte[]> consumeQueueContentList = consumeQueueMMapFileModel.readContent(consumeQueueOffset, msgCount);
        List<byte[]> commitLogBodyContentList = new ArrayList<>();
        for (byte[] content : consumeQueueContentList) {
            ConsumeQueueDetailModel consumeQueueDetailModel = new ConsumeQueueDetailModel();
            // 获取到消息的位置信息
            consumeQueueDetailModel.convertToModel(content);
            // System.out.println("consumeQueueDetailModel: " + JSON.toJSONString(consumeQueueDetailModel));
            CommitLogMMapFileModel commitLogMMapFileModel = CommonCache.getCommitLogMMapFileModelManager().get(topic);
            byte[] commitLogContent = commitLogMMapFileModel.readContent(consumeQueueDetailModel.getMsgIndex(), consumeQueueDetailModel.getMsgLength());
            commitLogBodyContentList.add(commitLogContent);
        }
          return commitLogBodyContentList;
    }

    /**
     * 更新 ConsumerQueue-offset 值
     *
     * @return
     */
    public boolean ack(String topic, String consumeGroup, Integer queueId) {
        ConsumeQueueOffsetModel.OffsetTable offsetTable = CommonCache.getConsumerQueueOffsetModel().getOffsetTable();
        Map<String, ConsumeQueueOffsetModel.ConsumerGroupDetail> topicConsumerGroupDetailMap = offsetTable.getTopicConsumerGroupDetail();
        ConsumeQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = topicConsumerGroupDetailMap.get(topic);
        Map<String, String> consumeQueueOffsetMap = consumerGroupDetail.getConsumerGroupDetailMap().get(consumeGroup);
        String offsetStrInfo = consumeQueueOffsetMap.get(String.valueOf(queueId));
        String[] offsetArr = offsetStrInfo.split("#");
        String filename = offsetArr[0];
        Integer currentOffset = Integer.parseInt(offsetArr[1]);
        // 加上一条消息长度，定位到下一条需要消费的消息开头
        currentOffset += 12;
        consumeQueueOffsetMap.put(String.valueOf(queueId), filename + "#" + currentOffset);
        return true;
    }
}
