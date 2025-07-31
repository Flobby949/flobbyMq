package top.flobby.mq.nameserver.event.spi.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.PullBrokerIpRespDto;
import top.flobby.mq.common.enums.BrokerRoleEnum;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.common.enums.RegistryTypeEnum;
import top.flobby.mq.common.event.Listener;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.PullBrokerIpEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-07-24 11:21
 **/


public class PullBrokerIpListener implements Listener<PullBrokerIpEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PullBrokerIpListener.class);


    @Override
    public void onReceive(PullBrokerIpEvent event) throws Exception {
        String pullRole = event.getRole();
        Map<String, ServiceInstance> serviceInstanceMap = CommonCache.getServiceInstanceManager().getServiceInstanceMap();

        PullBrokerIpRespDto respDto = new PullBrokerIpRespDto();
        List<String> addressList = new ArrayList<>();
        serviceInstanceMap.forEach((reqId, instance) -> {
                    if (!RegistryTypeEnum.BROKER.name().equals(instance.getRegistryType())) {
                        return;
                    }
                    Map<String, Object> attrs = instance.getAttrs();
                    String instanceRole = (String) attrs.get("role");
                    if (BrokerRoleEnum.MASTER.name().equalsIgnoreCase(pullRole)
                            && BrokerRoleEnum.MASTER.name().equalsIgnoreCase(instanceRole)) {
                        // LOGGER.info("拉取Broker主节点的ip");
                        addressList.add(instance.getIp() + ":" + instance.getPort());
                    } else if (BrokerRoleEnum.SLAVE.name().equalsIgnoreCase(pullRole)
                            && BrokerRoleEnum.SLAVE.name().equalsIgnoreCase(instanceRole)) {
                        // LOGGER.info("拉取Broker从节点的ip");
                        addressList.add(instance.getIp() + ":" + instance.getPort());
                    }else if (BrokerRoleEnum.SINGLE.name().equalsIgnoreCase(pullRole)
                            && BrokerRoleEnum.SINGLE.name().equalsIgnoreCase(instanceRole)) {
                        LOGGER.info("拉取Broker单机架构的ip");
                        addressList.add(instance.getIp() + ":" + instance.getPort());
                    }
                });
        respDto.setAddressList(addressList);
        respDto.setMsgId(event.getMsgId());
        event.getCtx().writeAndFlush(
                new TcpMsg(NameServerResponseCodeEnum.PULL_BROKER_ADDRESS_SUCCESS.getCode(), respDto)
        );
    }
}
