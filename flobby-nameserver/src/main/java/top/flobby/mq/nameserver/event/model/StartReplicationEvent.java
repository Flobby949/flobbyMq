package top.flobby.mq.nameserver.event.model;

import top.flobby.mq.common.event.model.Event;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 从节点首次连接主节点时发送的事件
 * @create : 2025-05-07 11:18
 **/

public class StartReplicationEvent extends Event {

    private String user;
    private String password;
    private String slaveIp;
    private Integer slavePort;

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

    public String getSlaveIp() {
        return slaveIp;
    }

    public void setSlaveIp(String slaveIp) {
        this.slaveIp = slaveIp;
    }

    public Integer getSlavePort() {
        return slavePort;
    }

    public void setSlavePort(Integer slavePort) {
        this.slavePort = slavePort;
    }
}
