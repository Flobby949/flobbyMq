package top.flobby.mq.nameserver.event.spi.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.nameserver.event.model.SlaveHeartBeatEvent;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 主从连接心跳消息监听
 * @create : 2025-05-07 17:16
 **/

public class SlaveHeartBeatListener implements Listener<SlaveHeartBeatEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlaveHeartBeatListener.class);

    @Override
    public void onReceive(SlaveHeartBeatEvent event) throws IllegalAccessException {
        // LOGGER.info("收到从节点心跳");
    }
}
