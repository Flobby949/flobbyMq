package top.flobby.mq.broker.config;

import io.netty.util.internal.StringUtil;
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
        // 将项目路径配置在启动参数中，否则空指针报错
        String mqHome = System.getProperty(BrokerConstants.FLOBBY_MQ_HOME);
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("FLOBBY_MQ_HOME is null");
        }
        globalProperties.setMqHome(mqHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
