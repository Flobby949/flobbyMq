package top.flobby.mq.nameserver.replication;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsgDecoder;
import top.flobby.mq.common.coder.TcpMsgEncoder;
import top.flobby.mq.common.utils.AssertUtil;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.config.NameServerProperties;
import top.flobby.mq.nameserver.event.EventBus;
import top.flobby.mq.nameserver.handler.MasterSlaveReplicationServerHandler;

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
                NameServerProperties.MasterSlaveReplicationProperties masterSlaveProperties = nameServerProperties.getMasterSlaveReplicationProperties();
                AssertUtil.isNotBlank(masterSlaveProperties.getMaster(), "master参数为空");
                AssertUtil.isNotBlank(masterSlaveProperties.getRole(), "role参数为空");
                AssertUtil.isNotBlank(masterSlaveProperties.getType(), "type参数为空");
                AssertUtil.isNotNull(masterSlaveProperties.getPort(), "port参数为空");
                break;
            case TRACE:
                NameServerProperties.TraceReplicationProperties traceProperties = nameServerProperties.getTraceReplicationProperties();
                AssertUtil.isNotBlank(traceProperties.getNextNode(), "nextNode参数为空");
                break;
        }
        return modeEnum;
    }

    // 根据参数，判断复制方式
    public void startReplicationTask(ReplicationModeEnum modeEnum) {
        if (modeEnum == null || ReplicationModeEnum.SINGLE.equals(modeEnum)) {
            return;
        }
        int port;
        if (modeEnum.equals(ReplicationModeEnum.MASTER_SLAVE)) {
            port = CommonCache.getNameServerProperties().getMasterSlaveReplicationProperties().getPort();
        } else if (modeEnum.equals(ReplicationModeEnum.TRACE)) {
            port = 0;
        } else {
            port = 0;
        }
        Thread replicationTask = new Thread(() -> {
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
                    if (modeEnum.equals(ReplicationModeEnum.MASTER_SLAVE)) {
                        channel.pipeline().addLast(new MasterSlaveReplicationServerHandler(new EventBus("replication-task")));
                    } else if (modeEnum.equals(ReplicationModeEnum.TRACE)) {
                        // TODO 后续添加链路handler
                        channel.pipeline().addLast(null);
                    }
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
        replicationTask.setName("replication-task");
        replicationTask.start();
    }



    // 启动netty进程 / 单机版本不启动
}
