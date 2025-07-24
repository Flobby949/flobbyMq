package top.flobby.mq.common.dto;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-24 11:19
 **/

public class PullBrokerIpReqDto extends BaseNameServerRemoteDto{
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
