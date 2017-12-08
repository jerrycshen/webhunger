package me.shenchao.webhunger.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry Shen
 * @since 0.1
 */
public class PageInfo implements Serializable {

    private Long page_id;

    private Host host;

    private String pageTitle;

    private String url;

    private String parentUrl;

    private String pageMd5;

    private int depth;

    private String x_frame_options;

    /**
     * 响应头
     */
    private Map<String, List<String>> responseHeader;

    private String rawText;


    private byte[] bytes;

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

    public Long getPage_id() {
        return page_id;
    }

    public void setPage_id(Long page_id) {
        this.page_id = page_id;
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

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
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

    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Map<String, List<String>> responseHeader) {
        this.responseHeader = responseHeader;
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

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
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
