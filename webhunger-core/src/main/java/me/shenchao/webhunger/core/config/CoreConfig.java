package me.shenchao.webhunger.core.config;

import me.shenchao.webhunger.exception.ConfigException;
import me.shenchao.webhunger.util.FileUtil;

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
public class CoreConfig {

    private static final String PORT = "5572";

    private static final String CONTEXT_PATH = "/webhunger";

    private boolean isStandalone = true;

    private String dataDir;

    private int port;

    private String contextPath;

    public void parse(String fileName) throws ConfigException, IOException {
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(fileName)) {
            properties.load(in);
        }
        parseProperties(properties);
        validateDataDirExist();
    }

    private void validateDataDirExist() throws ConfigException {
        if (!FileUtil.validateFileExist(dataDir)) {
            throw new ConfigException("Data dir not exists......");
        }
    }

    private void parseProperties(Properties properties) {
        isStandalone = Boolean.parseBoolean(properties.getProperty("standalone", "true"));
        dataDir = properties.getProperty("dataDir");
        port = Integer.parseInt(properties.getProperty("port", PORT));
        contextPath = properties.getProperty("contextPath", CONTEXT_PATH);
    }

    public boolean iStandalone() {
        return isStandalone;
    }

    public void setStandalone(boolean isStandalone) {
        this.isStandalone = isStandalone;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
