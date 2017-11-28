package me.shenchao.webhunger.config;

import me.shenchao.webhunger.exception.ConfigParseException;
import me.shenchao.webhunger.util.common.SystemUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 控制模块启动配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ControlConfig {

    private boolean distributed = false;

    private String taskAccessorJarDir = SystemUtil.getWebHungerDefaultDir() + File.separator + "accessor";

    private String taskAccessorClass = "me.shenchao.webhunger.client.task.FileTaskAccessor";

    /**
     * 支持几个站点同时爬取，默认-1，表示该任务下所有站点同时爬取，建议在分布式环境下设为-1，可加快全局爬取速度;
     */
    private int parallelism = -1;

    private String hostSchedulerClass = "me.shenchao.webhunger.control.scheduler.HostScheduler";

    private int port = 5572;

    private String contentPath = "/webhunger";

    public void parse(String fileName) throws IOException, ConfigParseException {
        parse(new FileInputStream(fileName));
    }

    public void parse(InputStream in) throws IOException, ConfigParseException {
        Properties properties = new Properties();
        properties.load(in);
        parseProperties(properties);
    }

    private void parseProperties(Properties properties) throws ConfigParseException {
        this.distributed = Boolean.parseBoolean(properties.getProperty("distributed", "false"));
        try {
            this.port = Integer.parseInt(properties.getProperty("port", "5572"));
        } catch (Exception e) {
            throw new ConfigParseException("port字段解析失败，使用默认值5572");
        }
        try {
            this.parallelism = Integer.parseInt(properties.getProperty("parallelism", "-1"));
            if (parallelism == 0) {
                this.parallelism = -1;
                throw new ConfigParseException("parallelism值不能为0，将使用默认值-1");
            }
        } catch (Exception e) {
            throw new ConfigParseException("parallelism字段解析失败，使用默认值-1");
        }

        this.taskAccessorJarDir = properties.getProperty("taskAccessorJarDir", taskAccessorClass);
        // 替换占位符
        this.taskAccessorJarDir = this.taskAccessorJarDir.replace("${webhunger.home}", SystemUtil.getWebHungerHomeDir());
        this.taskAccessorClass = properties.getProperty("taskAccessorClass", taskAccessorClass);
        this.hostSchedulerClass = properties.getProperty("hostSchedulerClass", hostSchedulerClass);
        this.contentPath = properties.getProperty("contextPath", contentPath);
    }

    public boolean isDistributed() {
        return distributed;
    }

    public String getTaskAccessorJarDir() {
        return taskAccessorJarDir;
    }

    public String getTaskAccessorClass() {
        return taskAccessorClass;
    }

    public int getPort() {
        return port;
    }

    public String getContentPath() {
        return contentPath;
    }

    public int getParallelism() {
        return parallelism;
    }

    public String getHostSchedulerClass() {
        return hostSchedulerClass;
    }
}
