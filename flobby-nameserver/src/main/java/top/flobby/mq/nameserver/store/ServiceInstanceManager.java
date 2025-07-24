package top.flobby.mq.nameserver.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 服务实例管理
 * @create : 2025-04-30 17:03
 **/

public class ServiceInstanceManager {
    private Map<String, ServiceInstance> serviceInstanceMap = new ConcurrentHashMap<>();

    public void putIfExist(ServiceInstance serviceInstance) {
        ServiceInstance currentInstance = this.get(serviceInstance.getIp(), serviceInstance.getPort());
        if (currentInstance != null && currentInstance.getFirstRegistryTime() != null) {
            serviceInstance.setFirstRegistryTime(currentInstance.getFirstRegistryTime());
        }
        serviceInstanceMap.put(serviceInstance.getIp() + ":" + serviceInstance.getPort(), serviceInstance);
    }

    public void put(ServiceInstance serviceInstance) {
        serviceInstanceMap.put(serviceInstance.getIp() + ":" + serviceInstance.getPort(), serviceInstance);
    }

    public ServiceInstance get(String brokerIp, Integer brokerPort) {
        return serviceInstanceMap.get(brokerIp + ":" + brokerPort);
    }

    public boolean remove(String key) {
        return serviceInstanceMap.remove(key) != null;
    }

    public Map<String, ServiceInstance> getServiceInstanceMap() {
        return serviceInstanceMap;
    }
}
