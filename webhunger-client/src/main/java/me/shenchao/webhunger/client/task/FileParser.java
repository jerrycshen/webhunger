package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostConfig;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.exception.TaskParseException;
import me.shenchao.webhunger.util.common.FileUtil;
import me.shenchao.webhunger.util.common.MD5Util;
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
            String fileName = FileUtil.getFileName(taskFile);
            task.setTaskId(MD5Util.get16bitMD5(fileName));
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
            Element intervalElement = configElement.element("leastInterval");
            if (intervalElement != null) {
                hostConfig.setLeastInterval(Integer.parseInt(intervalElement.getText()));
            }
            Element processorJarDirElement = configElement.element("processorJarDir");
            if (processorJarDirElement != null) {
                hostConfig.setProcessorJarDir(processorJarDirElement.getText());
            }
        }
        return hostConfig;
    }

    private static List<Host> parseHost(Task task, Element hostsElement) throws TaskParseException {
        if (hostsElement == null || hostsElement.elements("host").size() == 0)
            return new ArrayList<>();

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
            host.setHostId(MD5Util.get16bitMD5(host.getHostName()));
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
