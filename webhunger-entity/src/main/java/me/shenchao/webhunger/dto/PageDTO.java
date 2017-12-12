package me.shenchao.webhunger.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry Shen
 * @since 0.1
 */
public class PageDTO implements Serializable {

    private Long pageId;

    private String siteId;

    private String pageTitle;

    private String url;

    private String parentUrl;

    private String pageMd5;

    private int depth;

    private String x_frame_options;

    private String rawText;

    private String charset;

    /**
     * 自动检测是否有错
     */
    private boolean hasError = false;

    private boolean isSkip = false;

    public boolean isSkip() {
        return isSkip;
    }

    public void setSkip(boolean skip) {
        this.isSkip = skip;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageMd5() {
        return pageMd5;
    }

    public void setPageMd5(String pageMd5) {
        this.pageMd5 = pageMd5;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setResponseHeader(Map<String, List<String>> responseHeader) {
        List<String> values = responseHeader.get("X-Frame-Options");
        if (values != null) {
            setX_frame_options(values.get(0));
        }
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getX_frame_options() {
        return x_frame_options;
    }

    private void setX_frame_options(String x_frame_options) {
        this.x_frame_options = x_frame_options;
    }
}
