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

    public HostSnapshot(Host host, int state, Date createTime) {
        this.host = host;
        this.state = state;
        this.createTime = createTime;
    }

    public HostSnapshot() {}

    public void setHost(Host host) {
        this.host = host;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

}
