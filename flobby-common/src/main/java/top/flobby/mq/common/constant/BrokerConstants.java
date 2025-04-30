package top.flobby.mq.common.constant;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 常量类
 * @create : 2024-06-12 10:03
 **/

public class BrokerConstants {

    public final static String SPLIT = "/";

    public final static String FLOBBY_MQ_HOME = "flobby_mq_home";

    public final static String BASE_COMMIT_LOG_PATH = "/commit_log/";

    public final static String BASE_CONSUME_QUEUE_PATH = "/consume_queue/";

    public final static Integer MMAP_DEFAULT_START_OFFSET = 0;

    // 1mb 大小，方便开发，实际场景中一般是 1 GB
    public final static Integer COMMIT_LOG_DEFAULT_MMAP_SIZE = 1024 * 1024 * 1;

    // topic 刷新默认间隔
    public final static Long DEFAULT_REFRESH_MQ_TOPIC_INTERVAL = 10L;

    // ConsumerQueueOffset 刷新默认间隔
    public final static Long DEFAULT_REFRESH_CONSUMER_QUEUE_OFFSET_INTERVAL = 3L;
    /**
     * 每一段consumeQueueMsg的长度都是12byte，写一个常量管理
     */
    public static final Integer CONSUME_QUEUE_MSG_UNIT_SIZE = 12;
    public static final short DEFAULT_MAGIC_NUM = 17671;
}
