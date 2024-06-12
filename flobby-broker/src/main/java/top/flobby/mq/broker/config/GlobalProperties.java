package top.flobby.mq.broker.config;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 全局配置对象
 * @create : 2024-06-12 10:01
 **/

public class GlobalProperties {

    /**
     * 读取环境变量中配置的 mq 存储绝对路径
     */
    private String mqHome;

    public String getMqHome() {
        return mqHome;
    }

    public void setMqHome(String mqHome) {
        this.mqHome = mqHome;
    }
}
