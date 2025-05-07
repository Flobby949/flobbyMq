package top.flobby.mq.nameserver;

import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.config.PropertiesLoader;
import top.flobby.mq.nameserver.core.InValidServiceRemoveTask;
import top.flobby.mq.nameserver.core.NameServerStarter;
import top.flobby.mq.nameserver.replication.*;

import java.io.IOException;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : nameserver服务启动类
 * @create : 2025-04-30 10:38
 **/

public class NameServerStartUp {

    private static NameServerStarter nameServerStarter;
    private static ReplicationService replicationService = new ReplicationService();

    private static void initReplication() {
        // 复制逻辑初始化
        ReplicationModeEnum mode = replicationService.checkProperties();
        // 感觉角色启动netty进程
        replicationService.startReplicationTask(mode);
        if (mode.equals(ReplicationModeEnum.MASTER_SLAVE)) {
            ReplicationRoleEnum role = ReplicationRoleEnum.of(CommonCache.getNameServerProperties().getMasterSlaveReplicationProperties().getRole());
            ReplicationTask replicationTask = null;
            if (role.equals(ReplicationRoleEnum.MASTER)) {
                // 开启主从同步复制
                replicationTask = new MasterReplicationMsgSendTask("master-replication-msg-send-task");
                replicationTask.startTaskAsync();
            } else if (role.equals(ReplicationRoleEnum.SLAVE)) {
                // 开启心跳任务，发送给主节点
                replicationTask = new SlaveReplicationHeartBeatTask("slave-replication-heartbeat-send-task");
                replicationTask.startTaskAsync();
            }
            CommonCache.setReplicationTask(replicationTask);
        }
    }

    private static void initInvalidServiceRemoveTask() {
        // 启动非法服务剔除任务
        new Thread(new InValidServiceRemoveTask()).start();
    }

    // 1. 网络请求的接受 （netty完成）
    // 2. 事件发布的实现（eventBus -> event）Spring方式、Google Guava方式
    // 3. 事件处理器实现 （listener -> event）
    // 4. 数据存储（基于map本地内存方式存储）
    public static void main(String[] args) throws InterruptedException, IOException {
        // 加载配置
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        propertiesLoader.loadProperties();
        // 获取到配置后，判断集群的复制类型
        initReplication();
        // 启动非法服务剔除任务
        initInvalidServiceRemoveTask();
        // 启动服务
        nameServerStarter = new NameServerStarter(CommonCache.getNameServerProperties().getNameserverPort());
        nameServerStarter.startServer();
    }
}
