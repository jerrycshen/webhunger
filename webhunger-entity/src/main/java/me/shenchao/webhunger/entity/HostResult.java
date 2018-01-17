package me.shenchao.webhunger.entity;

import java.util.Date;

/**
 * 站点最终结果
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostResult {

    private Host host;
    private CrawledResult crawledResult;
    private ProcessedResult processedResult;
    private Date startTime;
    private Date endTime;

    public HostResult() {}

    public HostResult(Host host, CrawledResult crawledResult, ProcessedResult processedResult, Date startTime, Date endTime) {
        this.host = host;
        this.crawledResult = crawledResult;
        this.processedResult = processedResult;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public CrawledResult getCrawledResult() {
        return crawledResult;
    }

    public void setCrawledResult(CrawledResult crawledResult) {
        this.crawledResult = crawledResult;
    }

    public ProcessedResult getProcessedResult() {
        return processedResult;
    }

    public void setProcessedResult(ProcessedResult processedResult) {
        this.processedResult = processedResult;
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
