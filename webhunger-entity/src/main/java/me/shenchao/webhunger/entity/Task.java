package me.shenchao.webhunger.entity;

import java.util.Date;
import java.util.List;

/**
 * 封装每一个爬虫任务
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class Task {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称<br>
     *     实现时，该字段为非空字段
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务发起人
     */
    private String author;

    /**
     * 此次任务要爬取的站点
     */
    private transient List<Host> hosts;

    private HostConfig hostConfig;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date finishTime;

    /**
     * 任务状态:<br>
     *     <ul>
     *         <li>0: 还未开始</li>
     *         <li>1: 正在进行</li>
     *         <li>2: 已结束</li>
     *     </ul>
     */
    private int state;

    /**
     * 支持几个站点同时爬取。 默认-1，表示支持该task下所有站点同时爬取
     */
    private int parallelism = -1;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public HostConfig getHostConfig() {
        return hostConfig;
    }

    public void setHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public int getState() {
        Date today = new Date();
        if (this.startTime != null && this.startTime.after(today)) {
            setState(0);
        } else if (this.finishTime != null && this.finishTime.before(today)) {
            setState(2);
        } else {
            setState(1);
        }

        return state;
    }

    private void setState(int state) {
        this.state = state;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }
}
