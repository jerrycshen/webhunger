package me.shenchao.webhunger.entity;

/**
 * 站点爬取配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostConfig {

    private int depth = -1;

    private int interval = 2000;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
