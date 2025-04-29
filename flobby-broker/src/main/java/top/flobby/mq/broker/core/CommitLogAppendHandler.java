package top.flobby.mq.broker.core;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.constant.BrokerConstants;
import top.flobby.mq.broker.model.CommitLogMessageModel;

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
     *
     * @param topic   主题
     * @param content 内容
     */
    public void appendMsg(String topic, byte[] content) throws IOException {
        CommitLogMMapFileModel commitLogMMapFileModel =  CommonCache.getCommitLogMMapFileModelManager().get(topic);
        if (commitLogMMapFileModel == null) {
            throw new RuntimeException("topic is not exist");
        }
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setContent(content);
        // commitLogMessageModel.setSize(content.length);
        commitLogMMapFileModel.writeContent(commitLogMessageModel);
    }

}
