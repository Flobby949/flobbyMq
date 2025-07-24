package top.flobby.mq.nameserver.store;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 服务实例
 * @create : 2025-04-30 17:01
 **/

public class ServiceInstance {
    private String ip;
    private Integer port;
    private Long firstRegistryTime;
    private Long lastHeartBeatTime;
    /**
     * 元数据
     */
    private Map<String, String> attrs = new HashMap<>();

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Long getFirstRegistryTime() {
        return firstRegistryTime;
    }

    public void setFirstRegistryTime(Long firstRegistryTime) {
        this.firstRegistryTime = firstRegistryTime;
    }

    public Long getLastHeartBeatTime() {
        return lastHeartBeatTime;
    }

    public void setLastHeartBeatTime(Long lastHeartBeatTime) {
        this.lastHeartBeatTime = lastHeartBeatTime;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }
}
