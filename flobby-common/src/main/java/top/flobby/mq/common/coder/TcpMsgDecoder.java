package top.flobby.mq.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.flobby.mq.common.constant.NameServerConstants;

import java.util.List;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 消息解码器
 * @create : 2025-04-30 10:18
 **/

public class TcpMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> in) throws Exception {
        // byte buf 可读字节数大于 2 + 4 + 4（TcpMsg的最小长度），才能保证数据包可能是完整的
        if (byteBuf.readableBytes() > 2 + 4 + 4) {
            // 如果魔数和默认魔数值不等，证明消息是伪造的
            if (byteBuf.readShort() != NameServerConstants.DEFAULT_MAGIC_NUM) {
                ctx.close();
                return;
            }
            int code = byteBuf.readInt();
            int len = byteBuf.readInt();
            // 判断剩下的长度和len长度是否足够
            // 不用 = 判断长度，因为消息可能存在粘包
            if (byteBuf.readableBytes() < len) {
                // 剩余可读空间不足，消息不完整，关闭ctx
                ctx.close();
                return;
            }
            byte[] body = new byte[len];
            byteBuf.readBytes(body);
            TcpMsg  tcpMsg = new TcpMsg();
            tcpMsg.setMagic(NameServerConstants.DEFAULT_MAGIC_NUM);
            tcpMsg.setCode(code);
            tcpMsg.setLen(len);
            tcpMsg.setBody(body);
            in.add(tcpMsg);
        }
    }
}
