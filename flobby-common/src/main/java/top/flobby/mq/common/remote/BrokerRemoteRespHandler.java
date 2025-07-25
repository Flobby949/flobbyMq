package top.flobby.mq.common.remote;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.flobby.mq.common.cache.BrokerSyncFutureManager;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.dto.BaseBrokerRemoteDto;
import top.flobby.mq.common.dto.SendMsgToBrokerRespDto;
import top.flobby.mq.common.enums.BrokerResponseCodeEnum;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : broker的响应handler
 * @create : 2025-07-24 15:36
 **/

@ChannelHandler.Sharable
public class BrokerRemoteRespHandler extends SimpleChannelInboundHandler<TcpMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpMsg tcpMsg) throws Exception {
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        if (BrokerResponseCodeEnum.SEND_MSG_RESP.getCode() == code) {
            SendMsgToBrokerRespDto respDto = JSON.parseObject(body, SendMsgToBrokerRespDto.class);
            handleSyncResp(tcpMsg);
        }

    }

    private void handleSyncResp(TcpMsg tcpMsg) {
        byte[] body = tcpMsg.getBody();
        BaseBrokerRemoteDto respDto = JSON.parseObject(body, BaseBrokerRemoteDto.class);
        SyncFuture syncFuture = BrokerSyncFutureManager.get(respDto.getMsgId());
        if (syncFuture != null) {
            syncFuture.setResponse(tcpMsg);
        }
    }
}
