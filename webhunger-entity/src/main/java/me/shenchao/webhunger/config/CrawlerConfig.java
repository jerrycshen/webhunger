package me.shenchao.webhunger.config;

import me.shenchao.webhunger.exception.ConfigParseException;

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
    }

    public boolean isDistributed() {
        return distributed;
    }
}
