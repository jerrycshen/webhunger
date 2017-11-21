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
    private List<Host> hosts;

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
     * 任务状态
     */
    private int state;

    /**
     * 该目录用于存放用户自定义的Jar，例如自定义数据加载器、页面处理器等
     */
    private String clientJarDir;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getClientJarDir() {
        return clientJarDir;
    }

    public void setClientJarDir(String clientJarDir) {
        this.clientJarDir = clientJarDir;
    }
}
