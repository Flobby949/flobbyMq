package top.flobby.mq.common.dto;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-31 15:20
 **/

public class ConsumeMsgAckRespDto extends BaseBrokerRemoteDto {

    /**
     * ack状态
     * @see top.flobby.mq.common.enums.AckStatusEnum
     */
    private int ackStatus;

    public int getAckStatus() {
        return ackStatus;
    }

    public void setAckStatus(int ackStatus) {
        this.ackStatus = ackStatus;
    }
}
