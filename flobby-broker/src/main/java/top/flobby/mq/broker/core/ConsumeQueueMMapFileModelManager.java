package top.flobby.mq.broker.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 管理器
 * @create : 2024-07-15 09:52
 **/

public class ConsumeQueueMMapFileModelManager {
    public Map<String, List<ConsumeQueueMMapFileModel>> consumeQueueMMapFileModelMap = new HashMap<>();

    public void put(String topic, List<ConsumeQueueMMapFileModel> consumeQueueMMapFileModels) {
        consumeQueueMMapFileModelMap.put(topic, consumeQueueMMapFileModels);
    }

    public List<ConsumeQueueMMapFileModel> get(String topic) {
        return consumeQueueMMapFileModelMap.get(topic);
    }
}
