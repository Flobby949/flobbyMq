package top.flobby.mq.broker.core;

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

    private MMapFileModelManager mMapFileModelManager = new MMapFileModelManager();

    /**
     * MMap 预加载
     *
     * @throws IOException io异常
     */
    public void prepareMMapLoading(String topicName) throws IOException {
        MMapFileModel mMapFileModel = new MMapFileModel();
        mMapFileModel.loadFileInMMap(topicName, BrokerConstants.MMAP_DEFAULT_START_OFFSET, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
        mMapFileModelManager.put(topicName, mMapFileModel);
    }

    /**
     * 追加写入消息
     *
     * @param topic   主题
     * @param content 内容
     */
    public void appendMsg(String topic, byte[] content) throws IOException {
        MMapFileModel mMapFileModel = mMapFileModelManager.get(topic);
        if (mMapFileModel == null) {
            throw new RuntimeException("topic is not exist");
        }
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setContent(content);
        // commitLogMessageModel.setSize(content.length);
        mMapFileModel.writeContent(commitLogMessageModel);
    }

    public void readMsg(String topic) {
        MMapFileModel mMapFileModel = mMapFileModelManager.get(topic);
        if (mMapFileModel == null) {
            throw new RuntimeException("topic is not exist");
        }
        byte[] readContent = mMapFileModel.readContent(0, 1000);
        System.out.println(new String(readContent));
    }
}
