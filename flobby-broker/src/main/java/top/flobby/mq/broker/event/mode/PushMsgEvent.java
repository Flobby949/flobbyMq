package top.flobby.mq.broker.event.mode;

import top.flobby.mq.common.dto.MessageDto;
import top.flobby.mq.common.event.model.Event;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 推送消息事件
 * @create : 2025-07-24 17:10
 **/

public class PushMsgEvent extends Event {

    private MessageDto message;

    public MessageDto getMessage() {
        return message;
    }

    public void setMessage(MessageDto message) {
        this.message = message;
    }
}
