package top.flobby.mq.broker.event.mode;

import top.flobby.mq.common.dto.ConsumeMsgReqDto;
import top.flobby.mq.common.event.model.Event;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-30 16:34
 **/

public class ConsumeMsgEvent extends Event {

    private ConsumeMsgReqDto reqDto;

    public ConsumeMsgReqDto getReqDto() {
        return reqDto;
    }

    public void setReqDto(ConsumeMsgReqDto reqDto) {
        this.reqDto = reqDto;
    }
}
