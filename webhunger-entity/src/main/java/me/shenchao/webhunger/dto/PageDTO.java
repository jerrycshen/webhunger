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

    private String siteId;

    private String pageTitle;

    private String url;

    private String parentUrl;

    private String pageMd5;

    private Integer depth;

    private String rawText;

    private String charset;

    // TODO 响应头

}
