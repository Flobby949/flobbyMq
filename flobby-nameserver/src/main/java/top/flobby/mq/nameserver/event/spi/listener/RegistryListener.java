package top.flobby.mq.nameserver.event.spi.listener;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.ServiceRegistryRespDto;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.enums.ReplicationModeEnum;
import top.flobby.mq.nameserver.enums.ReplicationMsgTypeEnum;
import top.flobby.mq.nameserver.event.model.RegistryEvent;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;
import top.flobby.mq.nameserver.utils.NameServerUtil;

import java.util.UUID;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 注册事件监听
 * @create : 2025-04-30 16:17
 **/

public class RegistryListener implements Listener<RegistryEvent>{
    public static final Logger LOGGER = LoggerFactory.getLogger(RegistryListener.class);

    @Override
    public void onReceive(RegistryEvent event) throws Exception {
        // 安全认证，简单通过密码实现
        String user = event.getUser();
        String password = event.getPassword();
        ChannelHandlerContext ctx = event.getCtx();
        if (!NameServerUtil.checkUserAndPassword(user, password)) {
            // 注册失败
            ServiceRegistryRespDto respDto = new ServiceRegistryRespDto();
            respDto.setMsgId(event.getMsgId());
            TcpMsg errorMsg = new TcpMsg(NameServerResponseCodeEnum.ERROR_ACCESS.getCode(), respDto);
            // 回写失败消息
            ctx.writeAndFlush(errorMsg);
            ctx.close();
            throw new IllegalAccessException(NameServerResponseCodeEnum.ERROR_ACCESS.getDesc());
        }
        LOGGER.info("注册成功：{}", JSON.toJSONString(event));
        // 认证成功的话，设置一个标识
        ctx.channel().attr(AttributeKey.valueOf("reqId")).set(event.getIp() + ":" + event.getPort());
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setIp(event.getIp());
        serviceInstance.setPort(event.getPort());
        serviceInstance.setFirstRegistryTime(event.getTimestamp());
        CommonCache.getServiceInstanceManager().put(serviceInstance);
        ReplicationModeEnum modeEnum = ReplicationModeEnum.of(CommonCache.getNameServerProperties().getReplicationMode());
        if (modeEnum == null || modeEnum.equals(ReplicationModeEnum.SINGLE)) {
            // 单机架构，不需要复制，并且返回一个ack
            String msgId = event.getMsgId();
            ServiceRegistryRespDto respDto = new ServiceRegistryRespDto();
            respDto.setMsgId(msgId);
            TcpMsg ackMsg = new TcpMsg(NameServerResponseCodeEnum.REGISTRY_SUCCESS.getCode(), respDto);
            ctx.writeAndFlush(ackMsg);
            return;
        }
        // 如果当前是主从复制模式，且当前时主节点，则向队列中塞入对象
        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setMsgId(UUID.randomUUID().toString());
        replicationMsgEvent.setServiceInstance(serviceInstance);
        replicationMsgEvent.setCtx(ctx);
        replicationMsgEvent.setType(ReplicationMsgTypeEnum.REGISTRY);
        replicationMsgEvent.setTimestamp(System.currentTimeMillis());
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);
    }
}
