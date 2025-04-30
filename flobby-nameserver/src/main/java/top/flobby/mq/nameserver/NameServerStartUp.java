package top.flobby.mq.nameserver;

import top.flobby.mq.common.constant.NameServerConstants;
import top.flobby.mq.nameserver.core.NameServerStarter;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : nameserver服务启动类
 * @create : 2025-04-30 10:38
 **/

public class NameServerStartUp {

    private static NameServerStarter nameServerStarter;

    public static void main(String[] args) throws InterruptedException {
        // 1. 网络请求的接受 （netty完成）
        // 2. 事件发布的实现（eventBus -> event）Spring方式、Google Guava方式
        // 3. 事件处理器实现 （listener -> event）
        // 4. 数据存储（基于map本地内存方式存储）
        nameServerStarter = new NameServerStarter(NameServerConstants.DEFAULT_NAMESERVER_PORT);
        nameServerStarter.startServer();
    }
}
