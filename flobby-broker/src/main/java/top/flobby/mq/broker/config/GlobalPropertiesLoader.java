package top.flobby.mq.broker.config;

import io.netty.util.internal.StringUtil;
import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.common.constant.BrokerConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 全局配置加载器
 * @create : 2024-06-12 10:03
 **/

public class GlobalPropertiesLoader {

    private GlobalProperties globalProperties;

    public void loadProperties() {
        globalProperties = new GlobalProperties();
        // 将项目路径配置在启动参数中，否则空指针报错
        String mqHome = System.getProperty(BrokerConstants.FLOBBY_MQ_HOME);
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("FLOBBY_MQ_HOME is null");
        }
        globalProperties.setMqHome(mqHome);

        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get(mqHome + BrokerConstants.BROKER_PROPERTIES_PATH)));
            globalProperties.setNameserverIp(properties.getProperty("nameserver.ip"));
            globalProperties.setNameserverPort(Integer.parseInt(properties.getProperty("nameserver.port")));
            globalProperties.setNameserverUser(properties.getProperty("nameserver.user"));
            globalProperties.setNameserverPassword(properties.getProperty("nameserver.password"));

            globalProperties.setBrokerPort(Integer.parseInt(properties.getProperty("broker.port")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CommonCache.setGlobalProperties(globalProperties);
    }
}
