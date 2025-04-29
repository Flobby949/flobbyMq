package top.flobby.mq.broker.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : mmap 模型管理
 * @create : 2024-06-12 09:43
 **/

public class CommitLogMMapFileModelManager {

    /**
     * key：主题名称
     * value：文件的 mmap 对象
     */
    private Map<String, CommitLogMMapFileModel> mMapFileModelMap = new HashMap<>();

    public void put(String topic, CommitLogMMapFileModel commitLogMMapFileModel) {
        mMapFileModelMap.put(topic, commitLogMMapFileModel);
    }

    public CommitLogMMapFileModel get(String topic) {
        return mMapFileModelMap.get(topic);
    }
}
