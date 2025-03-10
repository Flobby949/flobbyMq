package top.flobby.mq.broker.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : ConsumerQueue 当前消费记录模型
 * @create : 2024-06-13 10:33
 **/

public class ConsumerQueueOffsetModel {

    private OffsetTable offsetTable;

    public OffsetTable getOffsetTable() {
        return offsetTable;
    }

    public void setOffsetTable(OffsetTable offsetTable) {
        this.offsetTable = offsetTable;
    }

    @Override
    public String toString() {
        return "ConsumerQueueOffsetModel{" +
                "offsetTable=" + offsetTable +
                '}';
    }

    public class OffsetTable {
        private Map<String, ConsumerGroupDetail> topicConsumerGroupDetail = new HashMap<>();

        public Map<String, ConsumerGroupDetail> getTopicConsumerGroupDetail() {
            return topicConsumerGroupDetail;
        }

        public void setTopicConsumerGroupDetail(Map<String, ConsumerGroupDetail> topicConsumerGroupDetail) {
            this.topicConsumerGroupDetail = topicConsumerGroupDetail;
        }

        @Override
        public String toString() {
            return "OffsetTable{" +
                    "topicConsumerGroupDetail=" + topicConsumerGroupDetail +
                    '}';
        }
    }

    public static class ConsumerGroupDetail {
        private Map<String, Map<String, String>> consumerGroupDetailMap = new HashMap<>();

        public Map<String, Map<String, String>> getConsumerGroupDetailMap() {
            return consumerGroupDetailMap;
        }

        public void setConsumerGroupDetailMap(Map<String, Map<String, String>> consumerGroupDetailMap) {
            this.consumerGroupDetailMap = consumerGroupDetailMap;
        }

        @Override
        public String toString() {
            return "ConsumerGroupDetail{" +
                    "consumerGroupDetailMap=" + consumerGroupDetailMap +
                    '}';
        }
    }
}
