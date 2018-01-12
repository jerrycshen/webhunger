package me.shenchao.webhunger.entity;

import com.google.common.collect.Lists;
import me.shenchao.webhunger.dto.ErrorPageDTO;

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

    private List<ErrorPageDTO> errorRequests = Lists.newCopyOnWriteArrayList();

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

    public AtomicInteger getErrorPageNum() {
        return errorPageNum;
    }

    public List<ErrorPageDTO> getErrorRequests() {
        return errorRequests;
    }

}
