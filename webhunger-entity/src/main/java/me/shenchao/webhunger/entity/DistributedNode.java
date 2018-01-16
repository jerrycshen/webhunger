package me.shenchao.webhunger.entity;

/**
 * 系统节点信息，例如爬虫节点、页面处理节点
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class DistributedNode {

    /**
     * 主机名
     */
    private String hostName;
    /**
     * IP
     */
    private String ip;
    /**
     * 运行状态
     */
    private State state;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getState() {
        return state.getValue();
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        READY(0), RUNNING(1);

        private int value;

        State(int state) {
            this.value = state;
        }

        public int getValue() {
            return value;
        }

        public static State valueOf(int value) {
            switch (value) {
                case 0:
                    return READY;
                case 1:
                    return RUNNING;
                default:
                    return null;
            }
        }
    }

    public enum NodeType {
        CRAWLER, PROCESSOR;
    }
}
