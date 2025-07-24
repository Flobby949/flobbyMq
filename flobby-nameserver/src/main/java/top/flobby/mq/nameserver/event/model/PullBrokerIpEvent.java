package top.flobby.mq.nameserver.event.model;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-24 11:20
 **/

public class PullBrokerIpEvent extends Event{
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
