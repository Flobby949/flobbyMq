package top.flobby.mq.client.producer;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.*;
import top.flobby.mq.common.enums.*;
import top.flobby.mq.common.remote.BrokerNettyRemoteClient;
import top.flobby.mq.common.remote.NameServerNettyRemoteClient;
import top.flobby.mq.common.utils.AssertUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 默认生产者
 *
 * @author : flobby
 * @date 2025/07/21
 */

public class DefaultProducerImpl implements Producer {
    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultProducerImpl.class);
    // 连接nameserver，发送心跳，拉取broker地址
    // 与broker建立连接，发送数据到broker

    private String nsIp;
    private Integer nsPort;
    private String nsUser;
    private String nsPassword;
    private NameServerNettyRemoteClient nameServerNettyRemoteClient;
    private List<String> brokerAddressList;
    // 所有的broker连接
    private Map<String, BrokerNettyRemoteClient> brokerNettyRemoteClientMap = new ConcurrentHashMap<>();

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

    public Map<String, BrokerNettyRemoteClient> getBrokerNettyRemoteClientMap() {
        return brokerNettyRemoteClientMap;
    }

    public void setBrokerNettyRemoteClientMap(Map<String, BrokerNettyRemoteClient> brokerNettyRemoteClientMap) {
        this.brokerNettyRemoteClientMap = brokerNettyRemoteClientMap;
    }

    public void start() {
        nameServerNettyRemoteClient = new NameServerNettyRemoteClient(nsIp, nsPort);
        nameServerNettyRemoteClient.buildConnection();
        if (doRegistry()) {
            // 开启心跳任务
            doHeartbeat();
            // 拉取broker地址，broker如何将ip上报到nameserver？
            fetchBrokerAddress(BrokerRoleEnum.MASTER.name());
            // 连接到broker节点上
            connectBroker();
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

    /**
     * 连接到broker
     */
    private void connectBroker() {
        AssertUtil.isNotEmpty(brokerAddressList, "Broker节点列表不能为空");
        brokerAddressList.forEach(brokerAddress -> {
            String[] addressArr = brokerAddress.split(":");
            BrokerNettyRemoteClient client = new BrokerNettyRemoteClient(addressArr[0], Integer.parseInt(addressArr[1]));
            client.buildConnection();
            this.getBrokerNettyRemoteClientMap().put(brokerAddress, client);
        });
    }

    @Override
    public SendResult send(MessageDto message) {
        // topic需要定位到具体的broker实例
        BrokerNettyRemoteClient client = getRemoteBrokerClient();
        String msgId = UUID.randomUUID().toString();
        message.setMsgId(msgId);
        message.setSendWay(MessageSendWayEnum.SYNC.ordinal());
        TcpMsg tcpMsg = new TcpMsg(BrokerEventCodeEnum.PUSH_MSG.getCode(), JSON.toJSONBytes(message));
        TcpMsg respMsg = client.sendSyncMsg(tcpMsg, msgId);
        SendMsgToBrokerRespDto respDto = JSON.parseObject(respMsg.getBody(), SendMsgToBrokerRespDto.class);
        int status = respDto.getStatus();
        SendResult sendResult = new SendResult();
        if (status == SendMsgToBrokerRespStatusEnum.SUCCESS.ordinal()) {
            sendResult.setSendStatus(SendStatus.SUCCESS);
        } else if (status == SendMsgToBrokerRespStatusEnum.FAIL.ordinal()){
            sendResult.setSendStatus(SendStatus.FAIL);
        }
        return sendResult;
    }

    @Override
    public void sendAsync(MessageDto message) {
        BrokerNettyRemoteClient client = getRemoteBrokerClient();
        String msgId = UUID.randomUUID().toString();
        message.setMsgId(msgId);
        message.setSendWay(MessageSendWayEnum.ASYNC.ordinal());
        TcpMsg tcpMsg = new TcpMsg(BrokerEventCodeEnum.PUSH_MSG.getCode(), JSON.toJSONBytes(message));
        client.sendAsyncMsg(tcpMsg);
    }

    private BrokerNettyRemoteClient getRemoteBrokerClient() {
        return this.getBrokerNettyRemoteClientMap().values().stream().collect(Collectors.toList()).get(0);
    }
}
