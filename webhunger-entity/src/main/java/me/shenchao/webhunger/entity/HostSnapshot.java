package me.shenchao.webhunger.entity;

import java.util.Date;

/**
 * 站点快照信息
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostSnapshot {

    private transient Host host;

    private int state;

    private Date createTime;

    private HostSnapshot(Builder builder) {
        this.host = builder.host;
        this.state = builder.state;
        this.createTime = builder.createTime;
    }

    public Host getHost() {
        return host;
    }

    public int getState() {
        return state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() {
        return "HostSnapshot{" +
                "host=" + host +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }

    public static class Builder {
        private Host host;
        private int state;
        private Date createTime;

        public Builder(Host host, int state, Date createTime) {
            this.host = host;
            this.state = state;
            this.createTime = createTime;
        }

        public HostSnapshot build() {
            return new HostSnapshot(this);
        }

    }
}
