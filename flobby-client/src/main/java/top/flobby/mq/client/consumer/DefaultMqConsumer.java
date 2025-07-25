package top.flobby.mq.client.consumer;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.HeartbeatDto;
import top.flobby.mq.common.dto.PullBrokerIpReqDto;
import top.flobby.mq.common.dto.PullBrokerIpRespDto;
import top.flobby.mq.common.dto.ServiceRegistryReqDto;
import top.flobby.mq.common.enums.BrokerRoleEnum;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.common.enums.RegistryTypeEnum;
import top.flobby.mq.common.remote.NameServerNettyRemoteClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 默认消费端
 * @create : 2025-07-25 10:50
 **/

public class DefaultMqConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMqConsumer.class);
    // 先从nameserver拉取broker地址
    // 对broker进行rpc访问，拉取数据
    // 返回ack到broker

    private String nsIp;
    private Integer nsPort;
    private String nsUser;
    private String nsPassword;
    private NameServerNettyRemoteClient nameServerNettyRemoteClient;
    private List<String> brokerAddressList;
    private MessageConsumeListener messageConsumeListener;

    public MessageConsumeListener getMessageConsumeListener() {
        return messageConsumeListener;
    }

    public void setMessageConsumeListener(MessageConsumeListener messageConsumeListener) {
        this.messageConsumeListener = messageConsumeListener;
    }

    public List<String> getBrokerAddressList() {
        return brokerAddressList;
    }

    public void setBrokerAddressList(List<String> brokerAddressList) {
        this.brokerAddressList = brokerAddressList;
    }

    public String getNsIp() {
        return nsIp;
    }

    public void setNsIp(String nsIp) {
        this.nsIp = nsIp;
    }

    public Integer getNsPort() {
        return nsPort;
    }

    public void setNsPort(Integer nsPort) {
        this.nsPort = nsPort;
    }

    public String getNsUser() {
        return nsUser;
    }

    public void setNsUser(String nsUser) {
        this.nsUser = nsUser;
    }

    public String getNsPassword() {
        return nsPassword;
    }

    public void setNsPassword(String nsPassword) {
        this.nsPassword = nsPassword;
    }

    public void start() {
        nameServerNettyRemoteClient = new NameServerNettyRemoteClient(nsIp, nsPort);
        nameServerNettyRemoteClient.buildConnection();
        if (doRegistry()) {
            // 开启心跳任务
            doHeartbeat();
            // 拉取broker地址，broker如何将ip上报到nameserver？
            fetchBrokerAddress(BrokerRoleEnum.MASTER.name());
        }
    }

    /**
     * 注册到 nameserver
     *
     * @return boolean
     */
    private boolean doRegistry() {
        ServiceRegistryReqDto reqDto = new ServiceRegistryReqDto();
        String msgId = UUID.randomUUID().toString();
        reqDto.setMsgId(msgId);
        reqDto.setUser(nsUser);
        reqDto.setPassword(nsPassword);
        reqDto.setRegistryType(RegistryTypeEnum.PRODUCER.name());
        TcpMsg registryMsg = new TcpMsg(NameServerEventCodeEnum.REGISTRY.getCode(), reqDto);
        TcpMsg registryResp = nameServerNettyRemoteClient.sendSyncMsg(registryMsg, msgId);
        if (registryResp.getCode() == NameServerResponseCodeEnum.REGISTRY_SUCCESS.getCode()) {
            LOGGER.info("Producer 注册成功");
            return true;
        } else {
            LOGGER.error("Producer 注册失败");
            return false;
        }
    }

    /**
     * 心跳任务
     */
    private void doHeartbeat() {

        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    LOGGER.info("发送心跳");
                    String msgId = UUID.randomUUID().toString();
                    HeartbeatDto heartbeatDto = new HeartbeatDto();
                    heartbeatDto.setMsgId(msgId);
                    TcpMsg heartbeatMsg = new TcpMsg(NameServerEventCodeEnum.HEART_BEAT.getCode(), heartbeatDto);
                    TcpMsg heartbeatResp = nameServerNettyRemoteClient.sendSyncMsg(heartbeatMsg, msgId);
                    LOGGER.info("心跳返回:{}", heartbeatResp);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "heartbeat-task").start();

    }

    /**
     * 获取broker地址
     *
     * @param role 角色
     */
    private void fetchBrokerAddress(String role) {
        PullBrokerIpReqDto reqDto = new PullBrokerIpReqDto();
        String msgId = UUID.randomUUID().toString();
        reqDto.setMsgId(msgId);
        reqDto.setRole(role);
        TcpMsg reqMsg = new TcpMsg(NameServerEventCodeEnum.PULL_BROKER_MASTER_IP.getCode(), reqDto);
        TcpMsg respMsg = nameServerNettyRemoteClient.sendSyncMsg(reqMsg, msgId);
        PullBrokerIpRespDto respDto = JSON.parseObject(respMsg.getBody(), PullBrokerIpRespDto.class);
        this.setBrokerAddressList(respDto.getAddressList());
        LOGGER.info("拉取到broker地址：{}", respMsg);
    }


}
