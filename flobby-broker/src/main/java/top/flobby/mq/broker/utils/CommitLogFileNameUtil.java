package top.flobby.mq.broker.utils;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : CommitLog 文件命名工具
 * @create : 2024-06-12 14:19
 **/

public class CommitLogFileNameUtil {

    /**
     * 构建第一个 CommitLog 名称
     *
     * @return {@link String }
     */
    public static String buildFirstCommitLogName () {
        return "00000000";
    }


    /**
     * CommitLog 文件名生成
     *
     * @param oldFileName 旧文件名
     * @return {@link String }
     */
    public static String incrCommitLogFileName(String oldFileName) {
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
