package top.flobby.mq.nameserver.core;

import org.slf4j.Logger;
import top.flobby.mq.common.constant.NameServerConstants;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.store.ServiceInstance;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 无效服务剔除任务
 * @create : 2025-04-30 17:41
 **/

public class InValidServiceRemoveTask implements Runnable {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(InValidServiceRemoveTask.class);
    
    @Override
    public void run() {
        while (true) {
            try {
                // 服务运行先休眠，等待map的填充
                TimeUnit.SECONDS.sleep(10);
                // 获取所有的服务实例
                Map<String, ServiceInstance> serviceInstanceMap =
                        CommonCache.getServiceInstanceManager().getServiceInstanceMap();
                // 迭代器遍历
                Iterator<String> iterator = serviceInstanceMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String brokerInfoKey = iterator.next();
                    ServiceInstance serviceInstance = serviceInstanceMap.get(brokerInfoKey);
                    if (serviceInstance.getLastHeartBeatTime() != null) {
                        // 剔除三次心跳间隔都没有上报记录的实例
                        if (System.currentTimeMillis() - serviceInstance.getLastHeartBeatTime() > NameServerConstants.DEFAULT_HEARTBEAT_BREAK * 3) {
                            LOGGER.info("Remove invalid service: {}", brokerInfoKey);
                            iterator.remove();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error removing invalid services: {},", e.getMessage(), e);
            }
        }
    }
}
