package top.flobby.mq.nameserver.core;

import top.flobby.mq.common.constant.BrokerConstants;

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
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
