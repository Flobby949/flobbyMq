package top.flobby.mq.client.consumer;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.*;
import top.flobby.mq.common.enums.*;
import top.flobby.mq.common.remote.BrokerNettyRemoteClient;
import top.flobby.mq.common.remote.NameServerNettyRemoteClient;
import top.flobby.mq.common.utils.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
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

    private String brokerRole = BrokerRoleEnum.SINGLE.name();
    private String topic;
    private String consumeGroup;
    private Integer queueId;
    private Integer batchSize;
    private Map<String, BrokerNettyRemoteClient> brokerNettyRemoteClientMap = new ConcurrentHashMap<>();

    private final CountDownLatch lock = new CountDownLatch(1);

    public void start() throws InterruptedException {
        nameServerNettyRemoteClient = new NameServerNettyRemoteClient(nsIp, nsPort);
        nameServerNettyRemoteClient.buildConnection();
        if (doRegistry()) {
            // 开启心跳任务
            doHeartbeat();
            // 拉取broker地址，broker如何将ip上报到nameserver？
            fetchBrokerAddress();
            // 连接到broker节点
            this.connectBroker();
            // 启动消费任务
            startConsumeMsgTask();
            lock.await();
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
        reqDto.setRegistryType(RegistryTypeEnum.CONSUMER.name());
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
     */
    private void fetchBrokerAddress() {
        PullBrokerIpReqDto reqDto = new PullBrokerIpReqDto();
        String msgId = UUID.randomUUID().toString();
        reqDto.setMsgId(msgId);
        reqDto.setRole(this.getBrokerRole());
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

    /**
     * 启动 消息消费 任务
     */
    private void startConsumeMsgTask () {
        Thread consumeTask = new Thread(() -> {
            //  TODO 不知道对应的topic对应哪个节点
            if (brokerRole.equals(BrokerRoleEnum.SINGLE.name())) {
                while (true) {
                    LOGGER.info("开始消费任务");
                    try {
                        String defaultBrokerAddress = brokerAddressList.get(0);
                        BrokerNettyRemoteClient client = this.getBrokerNettyRemoteClientMap().get(defaultBrokerAddress);
                        ConsumeMsgReqDto msgReqDto = new ConsumeMsgReqDto();
                        msgReqDto.setTopic(topic);
                        msgReqDto.setConsumeGroup(consumeGroup);
                        String msgId = UUID.randomUUID().toString();
                        msgReqDto.setMsgId(msgId);
                        TcpMsg reqMsg = new TcpMsg(BrokerEventCodeEnum.CONSUME_MSG.getCode(), msgReqDto);
                        // 发送RPC消息，拉取到消息内容
                        TcpMsg pullRespMsg = client.sendSyncMsg(reqMsg, msgId, 5000);
                        if (pullRespMsg == null) {
                            continue;
                        }
                        ConsumeMsgRespDto consumeMsgRespDto = JSON.parseObject(pullRespMsg.getBody(), ConsumeMsgRespDto.class);
                        List<ConsumeMsgRespDto.ConsumeMsgRespItem> consumeMsgRespList = consumeMsgRespDto.getConsumeMsgRespItemList();
                        LOGGER.info("拉取到消费数据：{}", JSON.toJSONString(consumeMsgRespList));
                        if (CollectionUtils.isNotEmpty(consumeMsgRespList)) {
                            consumeMsgRespList.forEach(respItem -> {
                                List<byte[]> commitLogContentList = respItem.getCommitLogContentList();
                                if (CollectionUtils.isEmpty(commitLogContentList)) {
                                    return;
                                }
                                List<ConsumeMessage> consumeMsgList = new ArrayList<>();
                                // 解析每条消息，封装并消费
                                commitLogContentList.forEach(content -> {
                                    ConsumeMessage consumeMessage = new ConsumeMessage();
                                    consumeMessage.setBody(content);
                                    consumeMessage.setQueueId(queueId);
                                    consumeMsgList.add(consumeMessage);
                                });
                                messageConsumeListener.consume(consumeMsgList);
                            });
                        }
                        TimeUnit.SECONDS.sleep(10);
                    } catch (Exception e) {
                        LOGGER.error("消费任务异常", e);
                    }
                }
            }
        });
        consumeTask.setName("consume-msg-task");
        consumeTask.start();
    }

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumeGroup() {
        return consumeGroup;
    }

    public void setConsumeGroup(String consumeGroup) {
        this.consumeGroup = consumeGroup;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Map<String, BrokerNettyRemoteClient> getBrokerNettyRemoteClientMap() {
        return brokerNettyRemoteClientMap;
    }

    public void setBrokerNettyRemoteClientMap(Map<String, BrokerNettyRemoteClient> brokerNettyRemoteClientMap) {
        this.brokerNettyRemoteClientMap = brokerNettyRemoteClientMap;
    }

    public String getBrokerRole() {
        return brokerRole;
    }

    public void setBrokerRole(String brokerRole) {
        this.brokerRole = brokerRole;
    }
}
