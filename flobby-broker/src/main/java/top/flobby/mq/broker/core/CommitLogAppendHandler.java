package top.flobby.mq.broker.core;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.constant.BrokerConstants;
import top.flobby.mq.common.dto.MessageDto;

import java.io.IOException;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 消息增加处理器
 * @create : 2024-06-12 09:47
 **/

public class CommitLogAppendHandler {

    /**
     * MMap 预加载
     *
     * @throws IOException io异常
     */
    public void prepareMMapLoading(String topicName) throws IOException {
        CommitLogMMapFileModel commitLogMMapFileModel = new CommitLogMMapFileModel();
        commitLogMMapFileModel.loadFileInMMap(topicName, BrokerConstants.MMAP_DEFAULT_START_OFFSET, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
        CommonCache.getCommitLogMMapFileModelManager().put(topicName, commitLogMMapFileModel);
    }

    /**
     * 追加写入消息
     */
    public void appendMsg(MessageDto message) throws IOException {
        CommitLogMMapFileModel commitLogMMapFileModel =  CommonCache.getCommitLogMMapFileModelManager().get(message.getTopic());
        if (commitLogMMapFileModel == null) {
            throw new RuntimeException("topic is not exist");
        }
        commitLogMMapFileModel.writeContent(message, true);
    }

}
