package top.flobby.mq.common.dto;

import top.flobby.mq.common.enums.RegistryTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 服务注册请求dto
 * @create : 2025-07-21 10:41
 **/

public class ServiceRegistryReqDto extends BaseNameServerRemoteDto{

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
