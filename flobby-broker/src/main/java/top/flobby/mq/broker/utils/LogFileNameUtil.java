package top.flobby.mq.broker.utils;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.constant.BrokerConstants;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : CommitLog 文件命名工具
 * @create : 2024-06-12 14:19
 **/

public class LogFileNameUtil {

    /**
     * 构建第一个 CommitLog 名称
     *
     * @return {@link String }
     */
    public static String buildFirstCommitLogName() {
        return "00000000";
    }

    /**
     * 构建CommitLog文件路径
     *
     * @param topicName         主题名称
     * @param commitLogFileName 文件名
     * @return {@link String }
     */
    public static String buildCommitLogFilePath(String topicName, String commitLogFileName) {
        return CommonCache.getGlobalProperties().getMqHome()
                + BrokerConstants.BASE_COMMIT_LOG_PATH
                + topicName + BrokerConstants.SPLIT
                + commitLogFileName;
    }

    /**
     * 构建 ConsumeQueue 文件路径
     *
     * @param topicName            主题名称
     * @param queueId              队列 ID
     * @param consumeQueueFileName 使用队列文件名
     * @return {@link String }
     */
    public static String buildConsumeQueueFilePath(String topicName, int queueId, String consumeQueueFileName) {
        return CommonCache.getGlobalProperties().getMqHome()
                + BrokerConstants.BASE_CONSUME_QUEUE_PATH
                + topicName + BrokerConstants.SPLIT
                + queueId + BrokerConstants.SPLIT + consumeQueueFileName;
    }


    /**
     * CommitLog 文件名生成
     *
     * @param oldFileName 旧文件名
     * @return {@link String }
     */
    public static String incrCommitLogFileName(String oldFileName) {
        return incrFileName(oldFileName);
    }

    /**
     * ConsumeQueue 文件名生成
     *
     * @param oldFileName 旧文件名
     * @return {@link String }
     */
    public static String incrConsumeQueueFileName(String oldFileName) {
        return incrFileName(oldFileName);
    }

    /**
     * 文件名生成底层
     *
     * @param oldFileName 旧文件名
     * @return {@link String }
     */
    private static String incrFileName(String oldFileName) {
        if (oldFileName.length() != 8) {
            throw new IllegalArgumentException("fileName length must be 8， oldFileName = " + oldFileName);
        }
        long fileIndex = Long.parseLong(oldFileName);
        fileIndex++;
        if (fileIndex > 99999999) {
            throw new IllegalArgumentException("FileName too big，FileName = " + fileIndex);
        }
        // 补零
        return String.format("%08d", fileIndex);
    }
}
