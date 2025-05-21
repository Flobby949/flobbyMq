package top.flobby.mq.nameserver.config;

/**
 * 主从复制配置映射
 *
 * @author flobby
 */

public class MasterSlaveReplicationProperties {

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