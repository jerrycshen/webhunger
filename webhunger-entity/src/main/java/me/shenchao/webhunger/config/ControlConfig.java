package me.shenchao.webhunger.config;

import me.shenchao.webhunger.exception.ConfigParseException;
import me.shenchao.webhunger.util.SystemUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * core config entity for bootstrap
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ControlConfig {

    private boolean distributed = false;

    private String taskAccessorJarDir = SystemUtil.getWebHungerDefaultDir() + File.separator + "accessor";

    private String taskAccessorClass = "me.shenchao.webhunger.client.task.FileTaskAccessor";

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
        this.taskAccessorJarDir = properties.getProperty("taskAccessorJarDir", taskAccessorClass);
        // 替换占位符
        this.taskAccessorJarDir = this.taskAccessorJarDir.replace("${webhunger.home}", SystemUtil.getWebHungerHomeDir());
        this.taskAccessorClass = properties.getProperty("taskAccessorClass", taskAccessorClass);
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
}
