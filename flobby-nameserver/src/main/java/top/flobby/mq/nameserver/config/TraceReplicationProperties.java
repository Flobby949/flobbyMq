package top.flobby.mq.nameserver.config;

/**
 * @author flobby
 */
public class TraceReplicationProperties {

        private String nextNode;
        private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setNextNode(String nextNode) {
            this.nextNode = nextNode;
        }

        public String getNextNode() {
            return nextNode;
        }
    }