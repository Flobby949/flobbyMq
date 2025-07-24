package top.flobby.mq.common.remote;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.flobby.mq.common.cache.NameServerSyncFutureManager;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.coder.TcpMsgDecoder;
import top.flobby.mq.common.coder.TcpMsgEncoder;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 和broker进行远程连接的客户端
 * @create : 2025-07-21 09:49
 **/

public class BrokerNettyRemoteClient {
    private String ip;
    private Integer port;

    public BrokerNettyRemoteClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;

    public void buildConnection() {
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new TcpMsgDecoder());
                socketChannel.pipeline().addLast(new TcpMsgEncoder());
                socketChannel.pipeline().addLast(new BrokerRemoteRespHandler());
            }
        });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(ip, port).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        throw new RuntimeException("connect Broker has error!");
                    }
                }
            });
            // 初始化建立链接
            channel = channelFuture.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送同步消息
     *
     * @param tcpMsg TCP消息
     * @return {@link TcpMsg }
     */
    public TcpMsg sendSyncMsg(TcpMsg tcpMsg, String msgId) {
        channel.writeAndFlush(tcpMsg);
        SyncFuture syncFuture = new SyncFuture();
        syncFuture.setMsgId(msgId);
        NameServerSyncFutureManager.put(msgId, syncFuture);
        try {
            return (TcpMsg) syncFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
