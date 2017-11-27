package us.codecraft.webmagic;

import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 新增 encode字段表示页面所用编码<br>
 * 新增 pageId 字段表示存入数据库中的id值<br>
 *
 * Object storing extracted result and urls to fetch.<br>
 * Not runnable safe.<br>
 * Crawler method：                                               <br>
 * {@link #putField(String, Object)}  save extracted result            <br>
 * {@link #addTargetRequest(String)} add urls to fetch                 <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class Page {

    private Request request;

    private ResultItems resultItems = new ResultItems();

    /**
     * 页面所用编码格式
     */
    private String charset;

    private String rawText;

    private Selectable url;

    /**
     * 响应头
     */
    private Map<String, List<String>> headers;

    private int statusCode;

    private boolean downloadSuccess = true;

    private byte[] bytes;


    private List<Request> targetRequests = new ArrayList<Request>();

    public Page() {
    }

    public static Page fail() {
        Page page = new Page();
        page.setDownloadSuccess(false);
        return page;
    }

    public Page setSkip(boolean skip) {
        resultItems.setSkip(skip);
        return this;

    }

    /**
     * store extract results
     *
     * @param key key
     * @param field field
     */
    public void putField(String key, Object field) {
        resultItems.put(key, field);
    }


    public List<Request> getTargetRequests() {
        return targetRequests;
    }

    /**
     * add url to fetch
     *
     * @param requestString requestString
     */
    public void addTargetRequest(String requestString) {
        Request request = incrementDepth(new Request(requestString));
        request.setParentUrl(this.request.getUrl());
        synchronized (targetRequests) {
            targetRequests.add(request);
        }
    }

    /**
     * add requests to fetch
     * TODO 发现源码是在循环爬取该页面时调用此方法，所以以后想要重试，需要对页面深度进行修改
     *
     * @param request request
     */
    public void addTargetRequest(Request request) {
        synchronized (targetRequests) {
//            targetRequests.add(incrementDepth(request));
            targetRequests.add(request);
        }
    }

    /**
     * 新增： 在加入队列之前，先加深度加一
     * @param request 新的请求
     */
    private Request incrementDepth(Request request) {
        request.setNowDepth(this.request.getNowDepth() + 1);
        return request;
    }

    /**
     * get url of current page
     *
     * @return url of current page
     */
    public Selectable getUrl() {
        return url;
    }

    public void setUrl(Selectable url) {
        this.url = url;
    }

    /**
     * get request of current page
     *
     * @return request
     */
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
        this.resultItems.setRequest(request);
    }

    public ResultItems getResultItems() {
        return resultItems;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRawText() {
        return rawText;
    }

    public Page setRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "Page{" +
                "request=" + request +
                ", resultItems=" + resultItems +
                ", charset='" + charset + '\'' +
                ", rawText='" + rawText + '\'' +
                ", headers=" + headers +
                ", statusCode=" + statusCode +
                ", downloadSuccess=" + downloadSuccess +
                ", bytes=" + Arrays.toString(bytes) +
                ", targetRequests=" + targetRequests +
                '}';
    }
}
