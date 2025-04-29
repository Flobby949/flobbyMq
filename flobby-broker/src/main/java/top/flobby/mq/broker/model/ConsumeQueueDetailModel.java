package top.flobby.mq.broker.model;

import top.flobby.mq.broker.utils.ByteConvertUtil;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : ConsumerQueue数据结构存储的最小单元对象
 * @create : 2024-06-13 09:17
 **/

public class ConsumeQueueDetailModel {

    /**
     * commitLog 文件名
     * 4 byte
     */
    private int commitLogFileName;

    /**
     * 消息开始索引
     */
    private int msgIndex;

    /**
     * 消息长度
     */
    private int msgLength;

    public int getCommitLogFileName() {
        return commitLogFileName;
    }

    public void setCommitLogFileName(int commitLogFileName) {
        this.commitLogFileName = commitLogFileName;
    }

    public int getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }

    @Override
    public String toString() {
        return "ConsumerQueueDetailModel{" +
                "commitLogFileName='" + commitLogFileName + '\'' +
                ", msgIndex=" + msgIndex +
                ", msgLength=" + msgLength +
                '}';
    }

    public byte[] convertToBytes() {
        byte[] fileBytes = ByteConvertUtil.intToBytes(commitLogFileName);
        byte[] msgIndexBytes = ByteConvertUtil.intToBytes(msgIndex);
        byte[] msgLengthBytes = ByteConvertUtil.intToBytes(msgLength);
        byte[] mergeBytes = new byte[fileBytes.length + msgIndexBytes.length + msgLengthBytes.length];
        int index = 0;
        for (int i = 0; i < fileBytes.length; i++, index++) {
            mergeBytes[index] = fileBytes[i];
        }
        for (int i = 0; i < msgIndexBytes.length; i++, index++) {
            mergeBytes[index] = msgIndexBytes[i];
        }
        for (int i = 0; i < msgLengthBytes.length; i++, index++) {
            mergeBytes[index] = msgLengthBytes[i];
        }
        return mergeBytes;
    }

    public void convertToModel(byte[] body) {
        // 0-3 -> 文件名
        byte[] fileName = ByteConvertUtil.readInPos(body, 0, 4);
        this.setCommitLogFileName(ByteConvertUtil.bytesToInt(fileName));
        // 4-7 -> msg当前的index
        byte[] msgIndex = ByteConvertUtil.readInPos(body, 4, 4);
        this.setCommitLogFileName(ByteConvertUtil.bytesToInt(msgIndex));
        // 8-11 -> msg的长度
        byte[] msgLength = ByteConvertUtil.readInPos(body, 8, 4);
        this.setCommitLogFileName(ByteConvertUtil.bytesToInt(msgLength));
    }
}
