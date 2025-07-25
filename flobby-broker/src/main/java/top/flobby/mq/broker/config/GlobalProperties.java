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

    // name server 配置
    private String nameserverIp;
    private Integer nameserverPort;
    private String nameserverUser;
    private String nameserverPassword;

    private Integer brokerPort;
    // 重平衡策略
    private String rebalanceStrategy;

    public String getRebalanceStrategy() {
        return rebalanceStrategy;
    }

    public void setRebalanceStrategy(String rebalanceStrategy) {
        this.rebalanceStrategy = rebalanceStrategy;
    }

    public Integer getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(Integer brokerPort) {
        this.brokerPort = brokerPort;
    }

    public String getMqHome() {
        return mqHome;
    }

    public void setMqHome(String mqHome) {
        this.mqHome = mqHome;
    }

    public String getNameserverIp() {
        return nameserverIp;
    }

    public void setNameserverIp(String nameserverIp) {
        this.nameserverIp = nameserverIp;
    }

    public Integer getNameserverPort() {
        return nameserverPort;
    }

    public void setNameserverPort(Integer nameserverPort) {
        this.nameserverPort = nameserverPort;
    }

    public String getNameserverUser() {
        return nameserverUser;
    }

    public void setNameserverUser(String nameserverUser) {
        this.nameserverUser = nameserverUser;
    }

    public String getNameserverPassword() {
        return nameserverPassword;
    }

    public void setNameserverPassword(String nameserverPassword) {
        this.nameserverPassword = nameserverPassword;
    }
}
