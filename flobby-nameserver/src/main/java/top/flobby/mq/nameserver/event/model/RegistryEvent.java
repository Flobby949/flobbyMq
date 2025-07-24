package top.flobby.mq.nameserver.event.model;

import top.flobby.mq.common.enums.RegistryTypeEnum;
import top.flobby.mq.common.event.model.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 注册事件，首次连接nameserver使用
 * @create : 2025-04-30 11:22
 **/

public class RegistryEvent extends Event {
    private String user;
    private String password;
    private String ip;
    private Integer port;
    /**
     * @see RegistryTypeEnum
     */
    private String registryType;
    private Map<String, Object> attrs = new HashMap<>();

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }
}
