package top.flobby.mq.broker.event.mode;

import top.flobby.mq.common.dto.ConsumeMsgAckReqDto;
import top.flobby.mq.common.event.model.Event;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-31 15:00
 **/

public class ConsumeMsgAckEvent extends Event {
    private ConsumeMsgAckReqDto consumeMsgAckReqDto;

    public void setConsumeMsgAckReqDto(ConsumeMsgAckReqDto consumeMsgAckReqDto) {
        this.consumeMsgAckReqDto = consumeMsgAckReqDto;
    }

    public ConsumeMsgAckReqDto getConsumeMsgAckReqDto() {
        return consumeMsgAckReqDto;
    }
}
