package top.flobby.mq.broker.config;

import top.flobby.mq.broker.cache.CommonCache;
import top.flobby.mq.broker.constant.BrokerConstants;

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
        String mqHome = System.getenv(BrokerConstants.FLOBBY_MQ_HOME);
        if (mqHome == null) {
            throw new IllegalArgumentException("FLOBBY_MQ_HOME is null");
        }
        globalProperties.setMqHome(mqHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
