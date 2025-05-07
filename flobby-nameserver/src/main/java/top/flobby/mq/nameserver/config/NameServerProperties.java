package top.flobby.mq.nameserver.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : nameserver属性配置
 * @create : 2025-05-07 10:07
 **/

public class NameServerProperties {
    private String nameserverUser;
    private String nameserverPassword;
    private Integer nameserverPort;
    private String replicationMode;
    private TraceReplicationProperties traceReplicationProperties;
    private MasterSlaveReplicationProperties masterSlaveReplicationProperties;

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

    public Integer getNameserverPort() {
        return nameserverPort;
    }

    public void setNameserverPort(Integer nameserverPort) {
        this.nameserverPort = nameserverPort;
    }

    public String getReplicationMode() {
        return replicationMode;
    }

    public void setReplicationMode(String replicationMode) {
        this.replicationMode = replicationMode;
    }

    public TraceReplicationProperties getTraceReplicationProperties() {
        return traceReplicationProperties;
    }

    public void setTraceReplicationProperties(TraceReplicationProperties traceReplicationProperties) {
        this.traceReplicationProperties = traceReplicationProperties;
    }

    public MasterSlaveReplicationProperties getMasterSlaveReplicationProperties() {
        return masterSlaveReplicationProperties;
    }

    public void setMasterSlaveReplicationProperties(MasterSlaveReplicationProperties masterSlaveReplicationProperties) {
        this.masterSlaveReplicationProperties = masterSlaveReplicationProperties;
    }

    public void print() {
        System.out.println(JSON.toJSONString(this, JSONWriter.Feature.PrettyFormat));
    }

    public static class TraceReplicationProperties {

        private String nextNode;

        public void setNextNode(String nextNode) {
            this.nextNode = nextNode;
        }

        public String getNextNode() {
            return nextNode;
        }
    }

    public static class MasterSlaveReplicationProperties {

        private String master;
        private String role;
        private String type;
        private Integer port;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public String getMaster() {
            return master;
        }
    }
}
