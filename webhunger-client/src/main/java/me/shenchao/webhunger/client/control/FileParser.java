package me.shenchao.webhunger.client.control;

import me.shenchao.webhunger.entity.*;
import me.shenchao.webhunger.exception.TaskParseException;
import me.shenchao.webhunger.util.common.FileUtils;
import me.shenchao.webhunger.util.common.MD5Utils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 2017-11-22
 *
 * @author Jerry Shen
 */
class FileParser {

    /**
     * 解析task文件
     * @param taskFile *.task
     * @return parsed task
     */
    static Task parseTask(File taskFile) throws TaskParseException {

        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new FileInputStream(taskFile));
            Element root = document.getRootElement();
            if (root.getName() != "task") {
                throw new TaskParseException("该文件不是task配置文件......");
            }
            Task task = new Task();
            // 计算task文件名的MD5 作为task的 ID
            String fileName = FileUtils.getFileName(taskFile);
            task.setTaskId(MD5Utils.get16bitMD5(fileName));
            Element nameElement = root.element("name");
            if (nameElement != null) {
                task.setTaskName(nameElement.getText());
            }
            Element authorElement = root.element("author");
            if (authorElement != null) {
                task.setAuthor(authorElement.getText());
            }
            Element descElement = root.element("description");
            if (descElement != null) {
                task.setDescription(descElement.getText());
            }
            Element startTimeElement = root.element("startTime");
            if (startTimeElement != null) {
                task.setStartTime(FileAccessSupport.parseDate(startTimeElement.getText()));
            }
            Element finishTimeElement = root.element("finishTime");
            if (finishTimeElement != null) {
                task.setFinishTime(FileAccessSupport.parseDate(finishTimeElement.getText()));
            }

            task.setHostConfig(parseHostConfig(root.element("config")));
            task.setHosts(parseHost(task, root.element("hosts")));

            return task;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TaskParseException("task配置文件解析失败......");
        }
    }

    private static HostConfig parseHostConfig(Element configElement) {
        HostConfig hostConfig = null;
        if (configElement != null) {
            hostConfig = new HostConfig();
            Element depthElement = configElement.element("depth");
            if (depthElement != null) {
                hostConfig.setDepth(Integer.parseInt(depthElement.getText()));
            }
            Element intervalElement = configElement.element("interval");
            if (intervalElement != null) {
                hostConfig.setInterval(Integer.parseInt(intervalElement.getText()));
            }
            Element charsetElement = configElement.element("charset");
            if (charsetElement != null) {
                hostConfig.setCharset(charsetElement.getText());
            }
            Element retryElement = configElement.element("retry");
            if (retryElement != null) {
                hostConfig.setRetry(Integer.parseInt(retryElement.getText()));
            }
            Element timeoutElement = configElement.element("timeout");
            if (timeoutElement != null) {
                hostConfig.setTimeout(Integer.parseInt(timeoutElement.getText()));
            }
            Element userAgentElement = configElement.element("userAgent");
            if (userAgentElement != null) {
                hostConfig.setUserAgent(userAgentElement.getText());
            }
            Element headersElement = configElement.element("headers");
            if (headersElement != null && headersElement.elements("header").size() > 0) {
                List<Element> headerList = headersElement.elements("header");
                for (Element headerElement : headerList) {
                    hostConfig.addHeader(headerElement.attributeValue("key"), headerElement.attributeValue("value"));
                }
            }
            Element cookiesElement = configElement.element("cookies");
            if (cookiesElement != null && cookiesElement.elements("cookie").size() > 0) {
                List<Element> cookieList = cookiesElement.elements("cookie");
                for (Element cookieElement : cookieList) {
                    hostConfig.addCookie(cookieElement.attributeValue("key"), cookieElement.attributeValue("value"));
                }
            }
            hostConfig.setUrlFilterConfig(parseURLFilterConfig(configElement.element("urlFilterConfig")));
        }
        return hostConfig;
    }

    private static URLFilterConfig parseURLFilterConfig(Element configElement) {
        URLFilterConfig urlFilterConfig = null;
        if (configElement != null) {
            urlFilterConfig = new URLFilterConfig();
            Element jarPathElement = configElement.element("urlFilterJarDir");
            if (jarPathElement != null) {
                urlFilterConfig.setUrlFilterJarDir(jarPathElement.getText());
            }
            Element filtersElement = configElement.element("filters");
            if (filtersElement != null && filtersElement.elements("filter").size() > 0) {
                List<Element> filterList = filtersElement.elements("filter");
                for (Element filterElement : filterList) {
                    urlFilterConfig.addFilterClass(filterElement.getText());
                }
            }
        }
        return urlFilterConfig;
    }

    private static List<Host> parseHost(Task task, Element hostsElement) throws TaskParseException {
        if (hostsElement == null || hostsElement.elements("host").size() == 0) {
            return new ArrayList<>();
        }

        Set<Host> hosts = new HashSet<>();
        List<Element> hostElements = hostsElement.elements("host");
        for (Element hostElement : hostElements) {
            Element hostNameElement = hostElement.element("hostName");
            Element hostIndexElement = hostElement.element("hostIndex");
            if (hostNameElement == null || hostIndexElement == null) {
                throw new TaskParseException("hostIndex结点与hostName结点必须存在......");
            }
            Host host = new Host();
            host.setTask(task);
            host.setHostIndex(hostIndexElement.getText());
            host.setHostName(hostNameElement.getText());
            // 根据host name 的MD5 值作为HOST ID
            host.setHostId(MD5Utils.get16bitMD5(host.getHostName()));
            hosts.add(host);

            host.setHostConfig(parseHostConfig(hostElement.element("config")));
        }

        List<Host> hostList = new ArrayList<>();
        hostList.addAll(hosts);

        return hostList;
    }

    static HostSnapshot parseSnapshot(String snapshotStr) {
        String[] fields = snapshotStr.split("\t");
        Host host = new Host();
        host.setHostId(fields[0]);
        return HostSnapshot.build()
                .setHost(host)
                .setState(Integer.parseInt(fields[1]))
                .setCreateTime(FileAccessSupport.parsePreciseDate(fields[2]));
    }
}
