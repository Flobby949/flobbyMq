package top.flobby.mq.common.dto;

import java.util.List;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 拉取brokerIp响应
 * @create : 2025-07-24 11:33
 **/

public class PullBrokerIpRespDto extends BaseNameServerRemoteDto{

    private List<String> addressList;

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }
}
