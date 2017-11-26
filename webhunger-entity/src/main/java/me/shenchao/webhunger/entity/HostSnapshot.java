package me.shenchao.webhunger.entity;

import java.util.Date;

/**
 * 站点快照信息
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostSnapshot {

    private Host host;

    private int successPageNum;

    private int errorPageNum;

    private int processedPageNum;

    private int state;

    private Date createTime;

    private HostSnapshot() {}

    public static HostSnapshot build() {
        return new HostSnapshot();
    }

    public Host getHost() {
        return host;
    }

    public HostSnapshot setHost(Host host) {
        this.host = host;
        return this;
    }

    public int getSuccessPageNum() {
        return successPageNum;
    }

    public HostSnapshot setSuccessPageNum(int successPageNum) {
        this.successPageNum = successPageNum;
        return this;
    }

    public int getErrorPageNum() {
        return errorPageNum;
    }

    public HostSnapshot setErrorPageNum(int errorPageNum) {
        this.errorPageNum = errorPageNum;
        return this;
    }

    public int getProcessedPageNum() {
        return processedPageNum;
    }

    public HostSnapshot setProcessedPageNum(int processedPageNum) {
        this.processedPageNum = processedPageNum;
        return this;
    }

    public int getState() {
        return state;
    }

    public HostSnapshot setState(int state) {
        this.state = state;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public HostSnapshot setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}
