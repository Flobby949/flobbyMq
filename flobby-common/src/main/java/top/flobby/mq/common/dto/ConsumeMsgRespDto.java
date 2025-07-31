package top.flobby.mq.common.dto;

import java.util.List;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消息消费返回dto
 * @create : 2025-07-30 16:51
 **/

public class ConsumeMsgRespDto extends BaseBrokerRemoteDto {

    private List<ConsumeMsgRespItem> consumeMsgRespItemList;

    public List<ConsumeMsgRespItem> getConsumeMsgRespItemList() {
        return consumeMsgRespItemList;
    }

    public void setConsumeMsgRespItemList(List<ConsumeMsgRespItem> consumeMsgRespItemList) {
        this.consumeMsgRespItemList = consumeMsgRespItemList;
    }

    public static class ConsumeMsgRespItem {
        private Integer queueId;

        private List<byte[]> commitLogContentList;

        public Integer getQueueId() {
            return queueId;
        }

        public void setQueueId(Integer queueId) {
            this.queueId = queueId;
        }

        public List<byte[]> getCommitLogContentList() {
            return commitLogContentList;
        }

        public void setCommitLogContentList(List<byte[]> commitLogContentList) {
            this.commitLogContentList = commitLogContentList;
        }
    }
}
