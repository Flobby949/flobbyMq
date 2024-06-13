package top.flobby.mq.broker.constant;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 常量类
 * @create : 2024-06-12 10:03
 **/

public class BrokerConstants {

    public final static String FLOBBY_MQ_HOME = "flobby_mq_home";

    public final static String BASE_STORE_PATH = "/commit_log/";

    public final static Integer MMAP_DEFAULT_START_OFFSET = 0;

    // 1mb 大小，方便开发，实际场景中一般是 1 GB
    public final static Integer COMMIT_LOG_DEFAULT_MMAP_SIZE = 1024 * 1024 * 1;

    // topic 刷新默认间隔
    public final static Long DEFAULT_REFRESH_MQ_TOPIC_INTERVAL = 10L;

    // ConsumerQueueOffset 刷新默认间隔
    public final static Long DEFAULT_REFRESH_CONSUMER_QUEUE_OFFSET_INTERVAL = 3L;
}
