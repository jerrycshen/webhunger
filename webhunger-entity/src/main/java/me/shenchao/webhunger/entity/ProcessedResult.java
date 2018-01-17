package me.shenchao.webhunger.entity;

import java.util.Date;

/**
 * 处理结果
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ProcessedResult {

    private transient Host host;
    private Date startTime;
    private Date endTime;

    public ProcessedResult() {}

    public ProcessedResult(Host host, Date startTime, Date endTime) {
        this.host = host;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
