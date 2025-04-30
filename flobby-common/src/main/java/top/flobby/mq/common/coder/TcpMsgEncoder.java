package top.flobby.mq.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消息编码器
 * @create : 2025-04-30 10:18
 **/

public class TcpMsgEncoder extends MessageToByteEncoder<TcpMsg> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TcpMsg msg, ByteBuf out) throws Exception {
        out.writeShort(msg.getMagic());
        out.writeInt(msg.getCode());
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getBody());
    }
}
