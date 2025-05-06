package top.flobby.mq.common.dto;

import java.io.Serializable;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 注册事件dto
 * @create : 2025-05-06 10:02
 **/

public class RegistryDto implements Serializable {

    private static final long serialVersionUID = -3355434084270665302L;

    private String user;
    private String password;
    private String brokerIp;
    private Integer brokerPort;

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

    public String getBrokerIp() {
        return brokerIp;
    }

    public void setBrokerIp(String brokerIp) {
        this.brokerIp = brokerIp;
    }

    public Integer getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(Integer brokerPort) {
        this.brokerPort = brokerPort;
    }
}
