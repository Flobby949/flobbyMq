package top.flobby.mq.common.coder;

import top.flobby.mq.common.constant.NameServerConstants;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : tcp消息
 * @create : 2025-04-30 10:19
 **/

public class TcpMsg {
    /**
     * 魔数
     */
    private short magic;

    /**
     * 请求包的具体含义
     * 根据code来判断body需要如何转换
     */
    private int code;
    /**
     * 消息长度
     */
    private int len;
    /**
     * 消息体
     */
    private byte[] body;

    public TcpMsg() {
    }

    public TcpMsg(int code, byte[] body) {
        this.code = code;
        this.magic = NameServerConstants.DEFAULT_MAGIC_NUM;
        this.len = body.length;
        this.body = body;
    }

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
