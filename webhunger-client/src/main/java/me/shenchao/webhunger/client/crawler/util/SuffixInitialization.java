package me.shenchao.webhunger.client.crawler.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 文件后缀名过滤 集合初始化.
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class SuffixInitialization {

    /**
     * 过滤URL的后缀名称集合
     */
    private static Set<String> pic_filter_suffix = new HashSet<>();

    private static Set<String> video_filter_suffix = new HashSet<>();

    private static Set<String> audio_filter_suffix = new HashSet<>();

    private static Set<String> document_filter_suffix = new HashSet<>();

    /**
     * 初始化过滤器
     */
    static  {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(SuffixInitialization.class.getResourceAsStream("/exclude_suffix.xml"));
            Element root = document.getRootElement();
            pic_filter_suffix = getSuffixByType(root, "pic_suffix");
            video_filter_suffix = getSuffixByType(root, "video_suffix");
            audio_filter_suffix = getSuffixByType(root, "audio_suffix");
            document_filter_suffix = getSuffixByType(root, "document_suffix");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> getSuffixByType(Element root, String type) {
        List<Node> nodes = root.selectNodes("//" + type + "/suffix");
        Set<String> suffix = new HashSet<>();
        for (Node node : nodes) {
            suffix.add(node.getText());
        }
        return suffix;
    }

    public static Set<String> getPic_filter_suffix() {
        return pic_filter_suffix;
    }

    public static Set<String> getVideo_filter_suffix() {
        return video_filter_suffix;
    }

    public static Set<String> getAudio_filter_suffix() {
        return audio_filter_suffix;
    }

    public static Set<String> getDocument_filter_suffix() {
        return document_filter_suffix;
    }

    public static Set<String> getAll_filter_suffix() {
        Set<String> allSuffix = new HashSet<>();
        allSuffix.addAll(pic_filter_suffix);
        allSuffix.addAll(video_filter_suffix);
        allSuffix.addAll(audio_filter_suffix);
        allSuffix.addAll(document_filter_suffix);
        return allSuffix;
    }
}
