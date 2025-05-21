package top.flobby.mq.nameserver.replication;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsgDecoder;
import top.flobby.mq.common.coder.TcpMsgEncoder;
import top.flobby.mq.common.utils.AssertUtil;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.config.MasterSlaveReplicationProperties;
import top.flobby.mq.nameserver.config.NameServerProperties;
import top.flobby.mq.nameserver.config.TraceReplicationProperties;
import top.flobby.mq.nameserver.enums.ReplicationModeEnum;
import top.flobby.mq.nameserver.enums.ReplicationRoleEnum;
import top.flobby.mq.nameserver.event.EventBus;
import top.flobby.mq.nameserver.handler.MasterReplicationServerHandler;
import top.flobby.mq.nameserver.handler.NodeSendReplicationMsgServerHandler;
import top.flobby.mq.nameserver.handler.NodeWriteMsgReplicationServerHandler;
import top.flobby.mq.nameserver.handler.SlaveReplicationServerHandler;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 复制服务
 * @create : 2025-05-07 10:36
 **/

public class ReplicationService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReplicationService.class);

    // 参数校验
    public ReplicationModeEnum checkProperties() {
        NameServerProperties nameServerProperties = CommonCache.getNameServerProperties();
        String replicationMode = nameServerProperties.getReplicationMode();
        if (StringUtils.isBlank(replicationMode) || ReplicationModeEnum.SINGLE.getMode().equals(replicationMode)) {
            LOGGER.error("执行单机模式");
            return ReplicationModeEnum.SINGLE;
        }
        ReplicationModeEnum modeEnum = ReplicationModeEnum.of(replicationMode);
        AssertUtil.isNotNull(modeEnum, "非法的复制模式类型");

        switch (modeEnum) {
            case MASTER_SLAVE:
                MasterSlaveReplicationProperties masterSlaveProperties = nameServerProperties.getMasterSlaveReplicationProperties();
                AssertUtil.isNotBlank(masterSlaveProperties.getMaster(), "master参数为空");
                AssertUtil.isNotBlank(masterSlaveProperties.getRole(), "role参数为空");
                AssertUtil.isNotBlank(masterSlaveProperties.getType(), "type参数为空");
                AssertUtil.isNotNull(masterSlaveProperties.getPort(), "port参数为空");
                break;
            case TRACE:
                TraceReplicationProperties traceProperties = nameServerProperties.getTraceReplicationProperties();
                AssertUtil.isNotNull(traceProperties.getPort(), "node节点端口为空");
                break;
        }
        return modeEnum;
    }

    // 根据参数，判断复制方式
    public void startReplicationTask(ReplicationModeEnum modeEnum) {
        if (modeEnum == null || ReplicationModeEnum.SINGLE.equals(modeEnum)) {
            return;
        }
        NameServerProperties nameServerProperties = CommonCache.getNameServerProperties();
        // 获取端口
        // 判断角色
        int port;
        ReplicationRoleEnum roleEnum;
        if (modeEnum.equals(ReplicationModeEnum.MASTER_SLAVE)) {
            port = nameServerProperties.getMasterSlaveReplicationProperties().getPort();
            roleEnum = ReplicationRoleEnum.of(nameServerProperties.getMasterSlaveReplicationProperties().getRole());
        } else {
            String nextNode = nameServerProperties.getTraceReplicationProperties().getNextNode();
            // 如果没有下一个节点，那就是尾部节点
            if (StringUtils.isNotBlank(nextNode)) {
                // 普通节点才需要开启netty，才需要端口
                roleEnum = ReplicationRoleEnum.NODE;
                port = nameServerProperties.getTraceReplicationProperties().getPort();
            } else {
                port = 0;
                roleEnum = ReplicationRoleEnum.TAIL;
            }
        }

        // Thread replicationTask = new Thread(() -> {
        if (roleEnum.equals(ReplicationRoleEnum.MASTER)) {
            startNettyServerAsync(new MasterReplicationServerHandler(new EventBus("master-replication-task")), port);
        } else if (roleEnum.equals(ReplicationRoleEnum.SLAVE)) {
            String masterAddress = nameServerProperties.getMasterSlaveReplicationProperties().getMaster();
            startNettyConnectAsync(new SlaveReplicationServerHandler(new EventBus("slave-replication-task")), masterAddress);
        } else if (roleEnum.equals(ReplicationRoleEnum.NODE)) {
            String nextNodeAddress = nameServerProperties.getTraceReplicationProperties().getNextNode();
            // 接收外部写入
            startNettyServerAsync(new NodeWriteMsgReplicationServerHandler(new EventBus("node-write-msg-replication-task")), port);
            // 向下一个节点发送同步消息
            startNettyConnectAsync(new NodeSendReplicationMsgServerHandler(new EventBus("node-send-replication-msg-task")), nextNodeAddress);
        } else if (roleEnum.equals(ReplicationRoleEnum.TAIL)) {
            startNettyServerAsync(new MasterReplicationServerHandler(new EventBus("tail-replication-task")), port);
        }
        // });
        // replicationTask.setName("replication-task");
        // replicationTask.start();
    }


    /**
     * 开启一个netty进程
     *
     * @param simpleChannelInboundHandler handler
     * @param port                        运行端口
     */
    private void startNettyServerAsync(SimpleChannelInboundHandler simpleChannelInboundHandler, int port) {
        Thread nettyServerTask = new Thread(() -> {
            // 负责netty的启动，防止同步阻塞，使用线程启动
            // 处理网络io的accept事件
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            // 处理网络io中的read和write事件
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    // 初始化编解码器，初始化handler服务
                    channel.pipeline().addLast(new TcpMsgDecoder());
                    channel.pipeline().addLast(new TcpMsgEncoder());
                    channel.pipeline().addLast(simpleChannelInboundHandler);
                }
            });
            // 监听jvm的关闭，进行优雅关闭
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                LOGGER.info("数据同步服务关闭成功");
            }));
            ChannelFuture channelFuture = null;
            try {
                channelFuture = bootstrap.bind(port).sync();
                LOGGER.info("数据同步服务启动成功，监听端口：{}", port);
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        nettyServerTask.setName("netty-server-task");
        nettyServerTask.start();
    }

    /**
     * netty连接
     *
     * @param simpleChannelInboundHandler handler
     * @param address                     地址
     */
    private void startNettyConnectAsync(SimpleChannelInboundHandler simpleChannelInboundHandler, String address) {
        Thread nettyConnectTask = new Thread(() -> {
            EventLoopGroup clientGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            Channel channel;

            bootstrap.group(clientGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TcpMsgDecoder());
                    ch.pipeline().addLast(new TcpMsgEncoder());
                    ch.pipeline().addLast(simpleChannelInboundHandler);
                }
            });
            ChannelFuture channelFuture = null;
            try {
                // 监听jvm的关闭，进行优雅关闭
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    clientGroup.shutdownGracefully();
                    LOGGER.info("从节点服务关闭成功");
                }));
                String[] addressArr = address.split(":");
                channelFuture = bootstrap.connect(addressArr[0], Integer.parseInt(addressArr[1])).sync();
                // 连接了master节点的channel对象，需要保存
                channel = channelFuture.channel();
                CommonCache.setMasterConnection(channel);
                LOGGER.info("成功连接到master节点：{}", address);
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        nettyConnectTask.setName("netty-connect-task");
        nettyConnectTask.start();
    }
}
