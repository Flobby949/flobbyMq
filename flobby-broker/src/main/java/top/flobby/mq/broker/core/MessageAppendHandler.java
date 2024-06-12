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
    private String filePath = "F:\\flobbyMq\\broker\\store\\order_cancel_topic\\00000001";
    public static String topicName = "order_cancel_topic";

    public MessageAppendHandler() throws IOException {
        prepareMMapLoading();
    }

    /**
     * MMap 预加载
     *
     * @throws IOException io异常
     */
    private void prepareMMapLoading() throws IOException {
        this.mMapFileModelManager = new MMapFileModelManager();
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

    public static void main(String[] args) throws IOException {
        MessageAppendHandler messageAppendHandler = new MessageAppendHandler();
        messageAppendHandler.readMsg(topicName);
        messageAppendHandler.appendMsg(topicName, "Hello World");
        messageAppendHandler.readMsg(topicName);
    }
}
