package me.shenchao.webhunger.entity;

/**
 * 站点爬取配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostConfig {

    private int depth = -1;

    private int leastInterval = 2000;

    /**
     * 该目录用于存放用户自定义的处理器Jar，例如URL处理器，页面处理器，站点处理器
     */
    private String processorJarDir;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getLeastInterval() {
        return leastInterval;
    }

    public void setLeastInterval(int leastInterval) {
        this.leastInterval = leastInterval;
    }

    public String getProcessorJarDir() {
        return processorJarDir;
    }

    public void setProcessorJarDir(String processorJarDir) {
        this.processorJarDir = processorJarDir;
    }
}
