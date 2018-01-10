package me.shenchao.webhunger.entity;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 站点状态数据统计
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class SiteStatusStatistics {

    private int totalPageNum;

    private int leftPageNum;

    private AtomicInteger successPageNum = new AtomicInteger(0);

    private AtomicInteger errorPageNum = new AtomicInteger(0);

    private List<String> errorRequests = Lists.newCopyOnWriteArrayList();

    private Date startTime;

    private Date endTime;

    public int getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(int totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public int getLeftPageNum() {
        return leftPageNum;
    }

    public void setLeftPageNum(int leftPageNum) {
        this.leftPageNum = leftPageNum;
    }

    public AtomicInteger getSuccessPageNum() {
        return successPageNum;
    }

    public void setSuccessPageNum(AtomicInteger successPageNum) {
        this.successPageNum = successPageNum;
    }

    public AtomicInteger getErrorPageNum() {
        return errorPageNum;
    }

    public void setErrorPageNum(AtomicInteger errorPageNum) {
        this.errorPageNum = errorPageNum;
    }

    public List<String> getErrorRequests() {
        return errorRequests;
    }

    public void setErrorRequests(List<String> errorRequests) {
        this.errorRequests = errorRequests;
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
