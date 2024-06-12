package top.flobby.mq.broker.core;

import java.io.IOException;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 消息增加处理器
 * @create : 2024-06-12 09:47
 **/

public class MessageAppendHandler {

    private MMapFileModelManager mMapFileModelManager = new MMapFileModelManager();

    /**
     * MMap 预加载
     *
     * @throws IOException io异常
     */
    public void prepareMMapLoading(String filePath, String topicName) throws IOException {
        MMapFileModel mMapFileModel = new MMapFileModel();
        mMapFileModel.loadFileInMMap(filePath, 0, 1024 * 1024 * 1);
        mMapFileModelManager.put(topicName, mMapFileModel);
    }

    /**
     * 追加写入消息
     *
     * @param topic     主题
     * @param content 内容
     */
    public void appendMsg(String topic, String content) {
        MMapFileModel mMapFileModel = mMapFileModelManager.get(topic);
        if (mMapFileModel == null) {
            throw new RuntimeException("topic is not exist");
        }
        mMapFileModel.writeContent(content.getBytes());
    }

    public void readMsg(String topic) {
        MMapFileModel mMapFileModel = mMapFileModelManager.get(topic);
        if (mMapFileModel == null) {
            throw new RuntimeException("topic is not exist");
        }
        byte[] readContent = mMapFileModel.readContent(0, 10);
        System.out.println(new String(readContent));
    }
}
