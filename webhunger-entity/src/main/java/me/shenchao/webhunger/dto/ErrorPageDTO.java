package me.shenchao.webhunger.dto;

/**
 * 错误页面
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ErrorPageDTO {

    private String hostId;
    private String url;
    private String parentUrl;
    private Integer depth;
    private Integer responseCode;
    private String errorMsg;

    public ErrorPageDTO(String hostId, String url, String parentUrl, Integer depth, Integer responseCode, String errorMsg) {
        this.hostId = hostId;
        this.url = url;
        this.parentUrl = parentUrl;
        this.depth = depth;
        this.responseCode = responseCode;
        this.errorMsg = errorMsg;
    }

    public ErrorPageDTO() {}

    public String getHostId() {
        return hostId;
    }

    public String getUrl() {
        return url;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public Integer getDepth() {
        return depth;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "ErrorPageDTO{" +
                "hostId='" + hostId + '\'' +
                ", url='" + url + '\'' +
                ", parentUrl='" + parentUrl + '\'' +
                ", depth=" + depth +
                ", responseCode=" + responseCode +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }

}
