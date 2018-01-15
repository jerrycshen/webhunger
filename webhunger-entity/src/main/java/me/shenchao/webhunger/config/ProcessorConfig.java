package me.shenchao.webhunger.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 处理模块启动配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ProcessorConfig {

    private boolean distributed = false;

    private String zkAddress;

    public void parse(String fileName) throws IOException {
        parse(new FileInputStream(fileName));
    }

    public void parse(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.load(in);
        parseProperties(properties);
    }

    private void parseProperties(Properties properties) {
        this.distributed = Boolean.parseBoolean(properties.getProperty("distributed", "false"));
        if (distributed) {
            this.zkAddress = properties.getProperty("zkAddress");
        }
    }

    public boolean isDistributed() {
        return distributed;
    }

    public String getZkAddress() {
        return zkAddress;
    }
}
