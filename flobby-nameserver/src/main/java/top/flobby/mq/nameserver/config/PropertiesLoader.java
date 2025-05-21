package top.flobby.mq.nameserver.config;

import top.flobby.mq.common.constant.BrokerConstants;
import top.flobby.mq.nameserver.cache.CommonCache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 配置加载类
 * @create : 2025-04-30 16:37
 **/

public class PropertiesLoader {
    private Properties properties = new Properties();

    public void loadProperties() throws IOException {
        String mqHome = System.getProperty(BrokerConstants.FLOBBY_MQ_HOME);
        properties.load(Files.newInputStream(new File(mqHome + "/config/nameserver.properties").toPath()));
        NameServerProperties nameServerProperties = new NameServerProperties();
        nameServerProperties.setNameserverPassword(getStr("nameserver.password"));
        nameServerProperties.setNameserverUser(getStr("nameserver.user"));
        nameServerProperties.setNameserverPort(getInt("nameserver.port"));
        nameServerProperties.setReplicationMode(getStr("nameserver.replication.mode"));
        TraceReplicationProperties traceReplicationProperties = new TraceReplicationProperties();
        traceReplicationProperties.setNextNode(getStrCanBeNull("nameserver.replication.trace.nextNode"));
        traceReplicationProperties.setPort(getInt("nameserver.replication.trace.port"));
        nameServerProperties.setTraceReplicationProperties(traceReplicationProperties);
        MasterSlaveReplicationProperties masterSlaveReplicationProperties = new MasterSlaveReplicationProperties();
        masterSlaveReplicationProperties.setMaster(getStrCanBeNull("nameserver.replication.master"));
        masterSlaveReplicationProperties.setRole(getStrCanBeNull("nameserver.replication.master-slave.role"));
        masterSlaveReplicationProperties.setType(getStrCanBeNull("nameserver.replication.master-slave.type"));
        masterSlaveReplicationProperties.setPort(getInt("nameserver.replication.port"));
        nameServerProperties.setMasterSlaveReplicationProperties(masterSlaveReplicationProperties);
        nameServerProperties.print();
        CommonCache.setNameServerProperties(nameServerProperties);
    }

    private String getStrCanBeNull(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    private String getStr(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("配置参数：" + key + "不存在");
        }
        return value;
    }

    private Integer getInt(String key) {
        return Integer.valueOf(getStr(key));
    }

    private Integer getIntCanBeNull(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }
        return Integer.valueOf(value);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
