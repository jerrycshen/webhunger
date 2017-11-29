package me.shenchao.webhunger.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 站点爬取配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HostConfig {

    private int depth = -1;

    private int interval = 2000;

    /**
     * 该目录用于存放用户自定义的处理器Jar，例如URL处理器，页面处理器，站点处理器
     */
    private String processorJarDir;

    private String charset;

    private int retry = 0;

    private int timeout = 5000;

    private String userAgent;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> cookies = new HashMap<>();

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

    public String getProcessorJarDir() {
        return processorJarDir;
    }

    public void setProcessorJarDir(String processorJarDir) {
        this.processorJarDir = processorJarDir;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }
}
