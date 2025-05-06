package top.flobby.mq.broker.netty.nameserver;

import io.netty.channel.Channel;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 心跳数据上报
 * @create : 2025-05-06 10:17
 **/

public class HeartBeatTaskManager {

    private AtomicInteger flag = new AtomicInteger(0);

    public void startTask() {
        // 防止重复请求
        if (flag.getAndIncrement() >= 1) {
            return;
        }
        Thread heartBeatTask = new Thread(new HeartBeatRequestTask());
        heartBeatTask.setName("heart-beat-task");
        heartBeatTask.start();
    }

    private class HeartBeatRequestTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    Channel channel = CommonCache.getNameServerClient().getChannel();
                    // 心跳包不需要额外透传过多参数，只需要告诉nameserver这个broker存活即可
                    TcpMsg tcpMsg = new TcpMsg(NameServerEventCodeEnum.HEART_BEAT);
                    channel.writeAndFlush(tcpMsg);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

}
