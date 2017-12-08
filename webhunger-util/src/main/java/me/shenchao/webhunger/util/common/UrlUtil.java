package me.shenchao.webhunger.util.common;

import org.apache.commons.lang3.StringUtils;
import java.util.regex.Pattern;

/**
 * URL 工具类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class UrlUtil {

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
}
