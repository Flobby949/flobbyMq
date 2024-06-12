package top.flobby.mq.broker.model;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : topic中的commitLog映射对象
 * @create : 2024-06-12 11:37
 **/

public class CommitLogModel {
    /**
     * 最新 CommitLog 文件名
     */
    private String fileName;
    /**
     * 最新 CommitLog 写入地址
     */
    private Long offset;
    /**
     * 可写入的最大体积
     */
    private Long offsetLimit;

    public Long getOffsetLimit() {
        return offsetLimit;
    }

    public void setOffsetLimit(Long offsetLimit) {
        this.offsetLimit = offsetLimit;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "CommitLogModel{" +
                "fileName='" + fileName + '\'' +
                ", offset=" + offset +
                ", offsetLimit=" + offsetLimit +
                '}';
    }
}
