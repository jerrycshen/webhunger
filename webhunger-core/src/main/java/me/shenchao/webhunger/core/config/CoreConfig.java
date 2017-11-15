package me.shenchao.webhunger.core.config;

import me.shenchao.webhunger.exception.ConfigException;
import me.shenchao.webhunger.util.FileUtil;

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

    private static final String DEFAULT_PORT = "5572";

    private static final String DEFAULT_CONTEXT_PATH = "/webhunger";

    private int port;

    private String contextPath;

    private boolean isStandalone = true;

    private String dataDir;

    public void parse(String fileName) throws ConfigException {
        try {
            Properties properties = new Properties();
            try (InputStream in = CoreConfig.class.getClassLoader().getResourceAsStream(fileName)) {
                properties.load(in);
            }
            parseProperties(properties);
            validateDataDirExist();
        } catch (IOException e) {
            throw new ConfigException("Processing failed......");
        }
    }

    private void validateDataDirExist() throws ConfigException {
        if (!FileUtil.validateFileExist(dataDir)) {
            throw new ConfigException("Data dir not exists......");
        }
    }

    private void parseProperties(Properties properties) {
        port = Integer.parseInt(properties.getProperty("port", DEFAULT_PORT));
        contextPath = properties.getProperty("contextPath", DEFAULT_CONTEXT_PATH);
        isStandalone = Boolean.parseBoolean(properties.getProperty("standalone", "true"));
        dataDir = properties.getProperty("dataDir");
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean iStandalone() {
        return isStandalone;
    }

    public void setStandalone(boolean isStandalone) {
        this.isStandalone = isStandalone;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
}
