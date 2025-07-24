package top.flobby.mq.client.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.HeartbeatDto;
import top.flobby.mq.common.dto.PullBrokerIpReqDto;
import top.flobby.mq.common.dto.ServiceRegistryReqDto;
import top.flobby.mq.common.enums.BrokerRoleEnum;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.common.enums.RegistryTypeEnum;
import top.flobby.mq.common.remote.NameServerNettyRemoteClient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 默认生产者
 *
 * @author : flobby
 * @date 2025/07/21
 */

public class DefaultProducer {
    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultProducer.class);
    // 连接nameserver，发送心跳，拉取broker地址
    // 与broker建立连接，发送数据到broker

    private String nsIp;
    private Integer nsPort;
    private String nsUser;
    private String nsPassword;
    private NameServerNettyRemoteClient nameServerNettyRemoteClient;

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
        LOGGER.info("拉取到broker地址：{}", respMsg);
    }
}
