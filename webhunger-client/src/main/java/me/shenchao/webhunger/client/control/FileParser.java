package me.shenchao.webhunger.client.control;

import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.entity.*;
import me.shenchao.webhunger.client.exceptioin.TaskParseException;
import me.shenchao.webhunger.util.common.FileUtils;
import me.shenchao.webhunger.util.common.MD5Utils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created on 2017-11-22
 *
 * @author Jerry Shen
 */
class FileParser {

    /**
     * 解析task文件
     * @param taskFile *.task
     * @param needHostParsed 是否需要进一步解析host
     * @return parsed task
     */
    static Task parseTask(File taskFile, boolean needHostParsed) {

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
                task.setStartTime(parseDate(startTimeElement.getText()));
            }
            Element finishTimeElement = root.element("finishTime");
            if (finishTimeElement != null) {
                task.setFinishTime(parseDate(finishTimeElement.getText()));
            }

            if (needHostParsed) {
                task.setHostConfig(parseHostConfig(root.element("config")));
                task.setHosts(parseHost(task, root.element("hosts")));
            }

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
            hostConfig.setUrlFilterConfig(parseUrlFilterConfig(configElement.element("urlFilterConfig")));
            hostConfig.setPageHandlerConfig(parsePageHandlerConfig(configElement.element("pageHandlerConfig")));
            hostConfig.setHostHandlerConfig(parseHostHandlerConfig(configElement.element("hostHandlerConfig")));
        }
        return hostConfig;
    }

    private static UrlFilterConfig parseUrlFilterConfig(Element configElement) {
        UrlFilterConfig urlFilterConfig = null;
        if (configElement != null) {
            urlFilterConfig = new UrlFilterConfig();
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

    private static PageHandlerConfig parsePageHandlerConfig(Element configElement) {
        PageHandlerConfig pageHandlerConfig = null;
        if (configElement != null) {
            pageHandlerConfig = new PageHandlerConfig();
            Element jarPathElement = configElement.element("handlerJarDir");
            if (jarPathElement != null) {
                pageHandlerConfig.setHandlerJarDir(jarPathElement.getText());
            }
            Element handlersElement = configElement.element("handlers");
            if (handlersElement != null && handlersElement.elements("handler").size() > 0) {
                List<Element> handlerList = handlersElement.elements("handler");
                for (Element handlerElement : handlerList) {
                    pageHandlerConfig.addHandlerClass(handlerElement.getText());
                }
            }
        }
        return pageHandlerConfig;
    }

    private static HostHandlerConfig parseHostHandlerConfig(Element configElement) {
        HostHandlerConfig hostHandlerConfig = null;
        if (configElement != null) {
            hostHandlerConfig = new HostHandlerConfig();
            Element jarPathElement = configElement.element("handlerJarDir");
            if (jarPathElement != null) {
                hostHandlerConfig.setHandlerJarDir(jarPathElement.getText());
            }
            Element handlersElement = configElement.element("handlers");
            if (handlersElement != null && handlersElement.elements("handler").size() > 0) {
                List<Element> handlerList = handlersElement.elements("handler");
                for (Element handlerElement : handlerList) {
                    hostHandlerConfig.addHandlerClass(handlerElement.getText());
                }
            }
        }
        return hostHandlerConfig;
    }

    private static List<Host> parseHost(Task task, Element hostsElement) {
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
            host.setHostId(MD5Utils.get16bitMD5(host.getHostIndex()));
            hosts.add(host);

            host.setHostConfig(parseHostConfig(hostElement.element("config")));
        }

        List<Host> hostList = new ArrayList<>();
        hostList.addAll(hosts);

        return hostList;
    }

    static HostSnapshot parseSnapshot(Host host, String snapshotStr) {
        String[] fields = snapshotStr.split("\t");
        return new HostSnapshot(host, Integer.parseInt(fields[1]),
                parsePreciseDate(fields[2]));
    }

    static CrawledResult parseCrawledResult(Host host, String crawledResultStr) {
        String[] fields = crawledResultStr.split("\t");
        return new CrawledResult(host, Integer.parseInt(fields[1]), Integer.parseInt(fields[2]),
                parsePreciseDate(fields[3]),parsePreciseDate(fields[4]));
    }

    static ProcessedResult parseProcessedResult(Host host, List<HostSnapshot> snapshots) {
        Date startTime = new Date(), endTime = new Date();
        for (HostSnapshot snapshot : snapshots) {
            if (snapshot.getState() == HostState.Crawling.getState()) {
                startTime = snapshot.getCreateTime();
            }
            if (snapshot.getState() == HostState.Completed.getState()) {
                endTime = snapshot.getCreateTime();
            }
        }
        return new ProcessedResult(host, startTime, endTime);
    }

    static ErrorPageDTO parseErrorPages(String hostId, String errorPageStr) {
        String[] fields = errorPageStr.split("\t");
        return new ErrorPageDTO(hostId, fields[2], fields[3],
                Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), fields[4]);
    }

    static Date parseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Date parsePreciseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String formatPreciseDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return formatter.format(date);
    }
}
