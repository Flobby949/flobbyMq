package top.flobby.mq.nameserver.utils;

import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.config.NameServerProperties;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description :
 * @create : 2025-05-07 14:33
 **/

public class NameServerUtil {

    /**
     * 检查用户和密码
     *
     * @param user     用户
     * @param password 密码
     * @return boolean
     */
    public static boolean checkUserAndPassword(String user, String password) {
        NameServerProperties nameServerProperties = CommonCache.getNameServerProperties();
        return nameServerProperties.getNameserverUser().equals(user) && nameServerProperties.getNameserverPassword().equals(password);
    }
}
