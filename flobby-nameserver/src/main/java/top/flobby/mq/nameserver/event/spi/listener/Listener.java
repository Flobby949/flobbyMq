package top.flobby.mq.nameserver.event.spi.listener;

import top.flobby.mq.nameserver.event.model.Event;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 事件监听器接口
 * @create : 2025-04-30 15:50
 **/

public interface Listener <E extends Event> {

    /**
     * 回调通知
     *
     * @param event event
     */
    void onReceive(E event) throws Exception;

}
