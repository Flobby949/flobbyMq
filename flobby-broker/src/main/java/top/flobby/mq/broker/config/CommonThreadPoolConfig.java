package top.flobby.mq.broker.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 通用线程池配置
 * @create : 2024-06-12 15:35
 **/

public class CommonThreadPoolConfig {

    /**
     * 用于 topic 配置异步刷盘
     */
    public static ThreadPoolExecutor refreshMqTopicExecutor = new ThreadPoolExecutor(
            1,
            1,
            30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10)
                    , r -> {
                Thread thread = new Thread(r);
                thread.setName("mq-topic-refresh-thread");
                return thread;
            }
    );

    /**
     * 用于 ConsumerQueue 的 Offset 异步刷盘
     */
    public static ThreadPoolExecutor refreshConsumerQueueOffsetExecutor = new ThreadPoolExecutor(
            1,
            1,
            30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10)
            , r -> {
        Thread thread = new Thread(r);
        thread.setName("mq-topic-refresh-thread");
        return thread;
    }
    );
}
