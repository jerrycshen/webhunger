package me.shenchao.webhunger.core.config;

/**
 * core config entity for bootstrap
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CoreConfig {

    private int port;

    private String context_path;

    private boolean isStandalone = true;

    private String data_dir;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContext_path() {
        return context_path;
    }

    public void setContext_path(String context_path) {
        this.context_path = context_path;
    }

    public boolean isStandalone() {
        return isStandalone;
    }

    public void setStandalone(boolean standalone) {
        isStandalone = standalone;
    }

    public String getData_dir() {
        return data_dir;
    }

    public void setData_dir(String data_dir) {
        this.data_dir = data_dir;
    }
}
