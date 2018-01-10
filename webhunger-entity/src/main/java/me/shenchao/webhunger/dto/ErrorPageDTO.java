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

    private ErrorPageDTO(Builder builder) {
        this.hostId = builder.hostId;
        this.url = builder.url;
        this.parentUrl = builder.parentUrl;
        this.depth = builder.depth;
        this.responseCode = builder.responseCode;
        this.errorMsg = builder.errorMsg;
    }

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

    public static class Builder {

        private final String hostId;
        private final String url;
        private String parentUrl;
        private Integer depth;
        private Integer responseCode;
        private String errorMsg;

        public Builder(String hostId, String url) {
            this.hostId = hostId;
            this.url = url;
        }

        public Builder parentUrl(String parentUrl) {
            this.parentUrl = parentUrl;
            return this;
        }

        public Builder depth(int depth) {
            this.depth = depth;
            return this;
        }

        public Builder responseCode(Integer responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder errorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public ErrorPageDTO build() {
            return new ErrorPageDTO(this);
        }
    }
}
