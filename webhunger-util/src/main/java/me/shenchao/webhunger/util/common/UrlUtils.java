package me.shenchao.webhunger.util.common;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * URL 工具类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class UrlUtils {

    private static Pattern patternForProtocal = Pattern.compile("[\\w]+://");

    public static String removeProtocol(String url) {
        return patternForProtocal.matcher(url).replaceAll("");
    }

    public static String getDomain(String url) {
        String domain = removeProtocol(url);
        int i = StringUtils.indexOf(domain, "/");
        if (i > 0) {
            domain = StringUtils.substring(domain, 0, i);
        }
        return domain;
    }

    public static String removePort(String domain) {
        int portIndex = domain.indexOf(":");
        if (portIndex != -1) {
            return domain.substring(0, portIndex);
        }else {
            return domain;
        }
    }

    /**
     *
     * 得到一个二级域名地址<br>
     *     例如域名：www.bdpf.org.cn，去掉主机名后为：bdpf.org.cn <br>
     *
     *  此方法主要用于判断该网页是否属于该站点之下<br>
     *      例如： service.bdpf.org.cn 就应该属于上述的域名，因此该网页应该爬取
     *
     * @param hostDomain 传入的host_domain已经是去掉端口，去掉协议，去掉最后的/ ，所以只需要提取就可以
     * @return 二级域名
     *
     * */
    public static String getHostSecondDomain(String hostDomain) {
        int dotIndex = hostDomain.indexOf(".");
        try {
            return hostDomain.substring(dotIndex+1);
        } catch (Exception ex) {
            throw new RuntimeException("domain不合法");
        }
    }

    /**
     * canonicalizeUrl
     * <br>
     * Borrowed from Jsoup.
     *
     * @param url url
     * @param refer refer
     * @return canonicalizeUrl
     */
    public static URL canonicalizeUrl(String url, String refer) {
        URL base;
        try {
            try {
                base = new URL(refer);
            } catch (MalformedURLException e) {
                // the base is unsuitable, but the attribute may be abs on its own, so try that
                URL abs = new URL(refer);
                return abs;
            }
            // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
            if (url.startsWith("?"))
                url = base.getPath() + url;
            return new URL(base, url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     *
     * 得到一个二级域名地址<br>
     *     例如域名：www.bdpf.org.cn，去掉主机名后为：bdpf.org.cn <br>
     *
     *  此方法主要用于判断该网页是否属于该站点之下<br>
     *      例如： service.bdpf.org.cn 就应该属于上述的域名，因此该网页应该爬取
     *
     * @param domain 传入的host_domain已经是去掉端口，去掉协议，去掉最后的/ ，所以只需要提取就可以
     * @return 二级域名
     *
     * */
    public static String getSecondLevelDomain(String domain) {
        int dotIndex = domain.indexOf(".");
        try {
            return domain.substring(dotIndex+1);
        } catch (Exception ex) {
            throw new RuntimeException("domain有误，请检查数据库中初始URL的合法性！");
        }
    }

    /**
     * 判断是否是无效链接
     * @param url url
     * @return true:无效链接
     */
    public static boolean isInValidUrl(String url) {
        return StringUtils.isBlank(url) || url.equals("#") || url.startsWith("javascript:") || url.startsWith("mailto:");
    }

    /**
     * 标准URL格式为：protocol :// hostname[:port] / path / [;parameters][?query]#fragment<br>
     *     如果带有fragment，可能会导致同一页面多次爬取，所以进一步判断之前，首先去掉该fragment
     *
     * @param url url
     * @return 如果带有fragment，则去掉，否则不变
     */
    public static String removeURLFragment(String url) {
        int fragment = url.indexOf("#");
        if (fragment != -1) {
            return url.substring(0, fragment);
        }
        return url;
    }

    /**
     * //TODO more charator support
     * 1. 转换空格字符 <br>
     * 2. 将url中非法字符进行转化，例如
     *     http://www.bdhxy.com/bdhzc/15/\ArticleList.aspx?id=176
     *     中的反斜杠转化为斜杠
     * @param url url
     * @return new url
     */
    public static String encodeIllegalCharacterInUrl(String url) {
        url = url.trim();
        url = url.replaceAll("\\\\", "/");
        // 替换url中空格，删除换行符
        return url.replaceAll(" ", "%20").replaceAll("\r|\n", "");
    }
}
