package me.shenchao.webhunger.dto;

import java.util.Date;
import java.util.List;

/**
 * 站点爬取过程中的快照，用于网络传输并在WEB UI中显示
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostSnapshotDTO {

    private String hostId;
    private Integer totalPageNum;
    private Integer leftPageNum;
    private Integer successPageNum;
    private Integer errorPageNum;
    private Date startTime;
    private Date endTime;
    private List<ErrorPageDTO> errorPages;

    public HostSnapshotDTO(Builder builder) {
        this.hostId = builder.hostId;
        this.totalPageNum = builder.totalPageNum;
        this.leftPageNum = builder.leftPageNum;
        this.successPageNum = builder.successPageNum;
        this.errorPageNum = builder.errorPageNum;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.errorPages = builder.errorPages;
    }

    public String getHostId() {
        return hostId;
    }

    public Integer getTotalPageNum() {
        return totalPageNum;
    }

    public Integer getLeftPageNum() {
        return leftPageNum;
    }

    public Integer getSuccessPageNum() {
        return successPageNum;
    }

    public Integer getErrorPageNum() {
        return errorPageNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public List<ErrorPageDTO> getErrorPages() {
        return errorPages;
    }

    @Override
    public String toString() {
        return "HostSnapshotDTO{" +
                "hostId='" + hostId + '\'' +
                ", totalPageNum=" + totalPageNum +
                ", leftPageNum=" + leftPageNum +
                ", successPageNum=" + successPageNum +
                ", errorPageNum=" + errorPageNum +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", errorPagesNum=" + errorPages.size() +
                '}';
    }

    public static class Builder {

        private final String hostId;
        private Integer totalPageNum;
        private Integer leftPageNum;
        private Integer successPageNum;
        private Integer errorPageNum;
        private Date startTime;
        private Date endTime;
        private List<ErrorPageDTO> errorPages;

        public Builder(String hostId) {
            this.hostId = hostId;
        }

        public Builder totalPageNum(Integer totalPageNum) {
            this.totalPageNum = totalPageNum;
            return this;
        }

        public Builder leftPageNum(Integer leftPageNum) {
            this.leftPageNum = leftPageNum;
            return this;
        }

        public Builder successPageNum(Integer successPageNum) {
            this.successPageNum = successPageNum;
            return this;
        }

        public Builder errorPageNum(Integer errorPageNum) {
            this.errorPageNum = errorPageNum;
            return this;
        }

        public Builder startTime(Date startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(Date endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder errorPages(List<ErrorPageDTO> errorPages) {
            this.errorPages = errorPages;
            return this;
        }

        public HostSnapshotDTO build() {
            return new HostSnapshotDTO(this);
        }
    }

}
