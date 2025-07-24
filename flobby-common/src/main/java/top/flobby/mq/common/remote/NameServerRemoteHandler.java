package top.flobby.mq.common.remote;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.flobby.mq.common.cache.NameServerSyncFutureManager;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.BaseNameServerRemoteDto;
import top.flobby.mq.common.enums.NameServerResponseCodeEnum;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 处理nameserver给客户端返回的数据内容
 * @create : 2025-07-21 10:35
 **/

@ChannelHandler.Sharable
public class NameServerRemoteHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        int code = tcpMsg.getCode();
        // byte[] body = tcpMsg.getBody();
        if (NameServerResponseCodeEnum.REGISTRY_SUCCESS.getCode() == code) {
            // ServiceRegistryRespDto respDto = JSON.parseObject(body, ServiceRegistryRespDto.class);
            // SyncFuture syncFuture = NameServerSyncFutureManager.get(respDto.getMsgId());
            // if (syncFuture != null) {
            //     syncFuture.setResponse(tcpMsg);
            // }
            this.handleSyncResp(tcpMsg);
        } else if (NameServerResponseCodeEnum.ERROR_ACCESS.getCode() == code) {
            // ServiceRegistryRespDto respDto = JSON.parseObject(body, ServiceRegistryRespDto.class);
            // SyncFuture syncFuture = NameServerSyncFutureManager.get(respDto.getMsgId());
            // if (syncFuture != null) {
            //     syncFuture.setResponse(tcpMsg);
            // }
            this.handleSyncResp(tcpMsg);
        } else if (NameServerResponseCodeEnum.HEART_BEAT_SUCCESS.getCode() == code) {
            // HeartbeatDto heartbeatDto = JSON.parseObject(body, HeartbeatDto.class);
            // SyncFuture syncFuture = NameServerSyncFutureManager.get(heartbeatDto.getMsgId());
            // if (syncFuture != null) {
            //     syncFuture.setResponse(tcpMsg);
            // }
            this.handleSyncResp(tcpMsg);
        } else if (NameServerResponseCodeEnum.PULL_BROKER_ADDRESS_SUCCESS.getCode() == code) {
            // PullBrokerIpRespDto respDto = JSON.parseObject(body, PullBrokerIpRespDto.class);
            // SyncFuture syncFuture = NameServerSyncFutureManager.get(respDto.getMsgId());
            // if (syncFuture != null) {
            //     syncFuture.setResponse(tcpMsg);
            // }
            this.handleSyncResp(tcpMsg);
        }
    }

    private void handleSyncResp(TcpMsg tcpMsg) {
        byte[] body = tcpMsg.getBody();
        BaseNameServerRemoteDto respDto = JSON.parseObject(body, BaseNameServerRemoteDto.class);
        SyncFuture syncFuture = NameServerSyncFutureManager.get(respDto.getMsgId());
        if (syncFuture != null) {
            syncFuture.setResponse(tcpMsg);
        }
    }
}
