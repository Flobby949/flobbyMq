package top.flobby.mq.nameserver.event.spi.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.RegistryEvent;
import top.flobby.mq.nameserver.store.ServiceInstance;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 注册事件监听
 * @create : 2025-04-30 16:17
 **/

public class RegistryListener implements Listener<RegistryEvent>{

    @Override
    public void onReceive(RegistryEvent event) throws IllegalAccessException {
        // 安全认证，简单通过密码实现
        String user = event.getUser();
        String password = event.getPassword();
        String propertyUser = CommonCache.getPropertiesLoader().getProperty("nameserver.user");
        String propertyPassword = CommonCache.getPropertiesLoader().getProperty("nameserver.password");
        ChannelHandlerContext ctx = event.getCtx();
        if (!user.equals(propertyUser) || !password.equals(propertyPassword)) {
            // 注册失败
            TcpMsg errorMsg = new TcpMsg(NameServerResponseCodeEnum.ERROR_USER_OR_PASSWORD);
            // 回写失败消息
            ctx.writeAndFlush(errorMsg);
            ctx.close();
            throw new IllegalAccessException(NameServerResponseCodeEnum.ERROR_USER_OR_PASSWORD.getDesc());
        }
        // 认证成功的话，设置一个标识
        ctx.channel().attr(AttributeKey.valueOf("reqId")).set(event.getBrokerIp() + ":" + event.getBrokerPort());
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setBrokerIp(event.getBrokerIp());
        serviceInstance.setBrokerPort(event.getBrokerPort());
        serviceInstance.setFirstRegistryTime(System.currentTimeMillis());
        CommonCache.getServiceInstanceManager().put(serviceInstance);
    }
}
