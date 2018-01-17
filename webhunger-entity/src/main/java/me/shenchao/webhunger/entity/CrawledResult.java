package me.shenchao.webhunger.entity;

import java.util.Date;

/**
 * 爬取结果
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CrawledResult {

    private transient Host host;
    private Integer totalPageNum;
    private Integer errorPageNum;
    private Date startTime;
    private Date endTime;

    public CrawledResult() {}

    public CrawledResult(Host host, Integer totalPageNum, Integer errorPageNum, Date startTime, Date endTime) {
        this.host = host;
        this.totalPageNum = totalPageNum;
        this.errorPageNum = errorPageNum;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getErrorPageNum() {
        return errorPageNum;
    }

    public void setErrorPageNum(Integer errorPageNum) {
        this.errorPageNum = errorPageNum;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Integer getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(Integer totalPageNum) {
        this.totalPageNum = totalPageNum;
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
