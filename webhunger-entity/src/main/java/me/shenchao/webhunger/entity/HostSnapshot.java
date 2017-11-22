package me.shenchao.webhunger.entity;

import java.util.Date;

/**
 * 站点快照信息
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostSnapshot {

    private String hostId;

    private int successPageNum;

    private int errorPageNum;

    private int processedPageNum;

    private int state;

    private Date createTime;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public int getSuccessPageNum() {
        return successPageNum;
    }

    public void setSuccessPageNum(int successPageNum) {
        this.successPageNum = successPageNum;
    }

    public int getErrorPageNum() {
        return errorPageNum;
    }

    public void setErrorPageNum(int errorPageNum) {
        this.errorPageNum = errorPageNum;
    }

    public int getProcessedPageNum() {
        return processedPageNum;
    }

    public void setProcessedPageNum(int processedPageNum) {
        this.processedPageNum = processedPageNum;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
