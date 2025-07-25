package top.flobby.mq.common.dto;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 发送消息到broker的响应
 * @create : 2025-07-24 16:36
 **/

public class SendMsgToBrokerRespDto extends BaseBrokerRemoteDto {

    /**
     * 0-成功
     * 1-失败
     * ....
     * 枚举的 ordinal
     * @see top.flobby.mq.common.enums.SendMsgToBrokerRespStatusEnum
     */
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
