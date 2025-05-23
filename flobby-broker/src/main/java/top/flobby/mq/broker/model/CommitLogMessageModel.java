package top.flobby.mq.broker.model;

import java.util.Arrays;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : CommitLog文件真实存储对象数据结构模型
 * @create : 2024-06-12 14:42
 **/

public class CommitLogMessageModel {

    /**
     * 消息体积大小，单位字节
     */
    // private int size;

    /**
     * 真正的消息内容
     */
    private byte[] content;

    @Override
    public String toString() {
        return "CommitLogMessageModel{" +
                "content=" + Arrays.toString(content) +
                '}';
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * 对象转换为字节数组
     *
     * @return {@link byte[] }
     */
    public byte[] convertToBytes() {
        // byte[] sizeByte = ByteConvertUtil.intToBytes(size);
        // byte[] mergeResult = new byte[sizeByte.length + content.length];
        // int index = 0;
        // for (int i = 0; i < sizeByte.length; i++, index++) {
        //     mergeResult[index] = sizeByte[i];
        // }
        // for (int i = 0; i < content.length; i++, index++) {
        //     mergeResult[index] = content[i];
        // }
        // return mergeResult;
        return content;
    }
}
