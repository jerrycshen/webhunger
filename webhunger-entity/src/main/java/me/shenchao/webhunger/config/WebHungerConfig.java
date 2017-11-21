package me.shenchao.webhunger.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * core config entity for bootstrap
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class WebHungerConfig {

    private Map<String, String> confMap = new HashMap<>();

    public void parse(String fileName) throws IOException {
        parse(new FileInputStream(fileName));
    }

    public void parse(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.load(in);
        parseProperties(properties);
    }

    private void parseProperties(Properties properties) {
        for (String key : properties.stringPropertyNames()) {
            confMap.put(key, properties.getProperty(key));
        }
    }

    public Map<String, String> getConfMap() {
        return confMap;
    }
}
