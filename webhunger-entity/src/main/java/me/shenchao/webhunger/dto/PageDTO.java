package me.shenchao.webhunger.dto;

import java.io.Serializable;

/**
 * 待处理页面
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class PageDTO implements Serializable {

    private Long pageId;

    private String hostId;

    private String url;

    private String parentUrl;

    private String pageMd5;

    private Integer depth;

    private String rawText;

    private String charset;

    private boolean skip;

    // TODO 响应头


    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
