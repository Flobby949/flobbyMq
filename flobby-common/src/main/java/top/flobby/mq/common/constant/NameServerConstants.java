package top.flobby.mq.common.constant;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 注册中心常量管理
 * @create : 2025-04-30 11:04
 **/

public interface NameServerConstants {

    // TCP消息默认魔数
    short DEFAULT_MAGIC_NUM = 17671;

    // 注册中心默认端口
    int DEFAULT_NAMESERVER_PORT = 8080;

    // 默认心跳间隔 3 s
    int DEFAULT_HEARTBEAT_BREAK = 3000;
}
