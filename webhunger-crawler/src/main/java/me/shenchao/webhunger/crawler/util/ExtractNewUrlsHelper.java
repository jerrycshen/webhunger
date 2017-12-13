package me.shenchao.webhunger.crawler.util;

import me.shenchao.webhunger.entity.webmagic.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * 抽取页面中所有url
 *
 * @since 0.1
 * @author Jerry Shen
 */
public class ExtractNewUrlsHelper {

    public static Set<String> extractAllUrls(Page page) {
        Document doc = Jsoup.parse(page.getRawText());
        Set<String> newUrls = new HashSet<>();
        getLinksUrls(doc, newUrls);
        getImportsUrls(doc, newUrls);
        getMediaUrls(doc, newUrls);
        return newUrls;
    }

    private static void getMediaUrls(Document doc, Set<String> urls) {
        Elements medias = doc.select("[src]");
        for (Element media : medias) {
            urls.add(media.attr("src"));
        }
    }

    private static void getImportsUrls(Document doc, Set<String> urls) {
        Elements imports = doc.select("link[href]");
        for (Element link : imports) {
            urls.add(link.attr("href"));
        }
    }

    private static void getLinksUrls(Document doc, Set<String> urls) {
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            urls.add(link.attr("href"));
        }
    }

}
