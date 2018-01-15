package me.shenchao.webhunger.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 爬虫模块启动配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CrawlerConfig {

    private boolean distributed = false;

    private String zkAddress;

    private String redisAddress;

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
            this.redisAddress = properties.getProperty("redisAddress");
        }
    }

    public boolean isDistributed() {
        return distributed;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public String getRedisAddress() {
        return redisAddress;
    }

    @Override
    public String toString() {
        return "CrawlerConfig{" +
                "distributed=" + distributed +
                ", zkAddress='" + zkAddress + '\'' +
                ", redisAddress='" + redisAddress + '\'' +
                '}';
    }
}
