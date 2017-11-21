package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.client.TaskLoader;
import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostConfig;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.exception.TaskParseException;
import me.shenchao.webhunger.util.FileUtil;
import me.shenchao.webhunger.util.SystemUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 从文件中读取任务信息
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class FileTaskLoader implements TaskLoader {

    private static final Logger logger = LoggerFactory.getLogger(FileTaskLoader.class);

    private static final String DEFAULT_TASK_PATH = SystemUtil.getWebHungerUserDir() + File.separator + "task";

    @Override
    public List<Task> loadTasks(WebHungerConfig webHungerConfig) {
        List<Task> tasks = new ArrayList<>();
        // 1. 获取task配置文件
        File[] taskFiles = getTaskFiles(webHungerConfig);

        // 2. 解析task配置文件
        for (File taskFile : taskFiles) {
            try {
                tasks.add(parseTask(taskFile));
            } catch (TaskParseException e) {
                logger.error("解析{}失败，请检查文件格式......{}", taskFile.getAbsoluteFile(), e);
                e.printStackTrace();
            }
        }
        return tasks;
    }

    /**
     * 解析task文件
     * @param taskFile *.task
     * @return parsed task
     */
    private Task parseTask(File taskFile) throws TaskParseException {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new FileInputStream(taskFile));
            Element root = document.getRootElement();
            if (root.getName() != "task") {
                throw new TaskParseException("该文件不是task配置文件......");
            }
            Task task = new Task();
            // 使用task文件名作为task的 ID
            task.setTaskId(FileUtil.getFileName(taskFile));
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
                task.setStartTime(transferDate(startTimeElement.getText()));
            }
            Element finishTimeElement = root.element("finishTime");
            if (finishTimeElement != null) {
                task.setFinishTime(transferDate(finishTimeElement.getText()));
            }

            task.setHostConfig(parseHostConfig(root.element("config")));
            task.setHosts(parseHost(task, root.element("hosts")));

            return task;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TaskParseException("task配置文件解析失败......");
        }
    }

    private HostConfig parseHostConfig(Element configElement) {
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
        }
        return hostConfig;
    }

    private List<Host> parseHost(Task task, Element hostsElement) throws TaskParseException {
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
            host.setHostId(SystemUtil.generateRandomId());
            host.setHostIndex(hostIndexElement.getText());
            host.setHostName(hostNameElement.getText());
            hosts.add(host);

            host.setHostConfig(parseHostConfig(hostElement.element("config")));
        }

        List<Host> hostList = new ArrayList<>();
        hostList.addAll(hosts);

        return hostList;
    }

    /**
     * 从指定目录下找到所有以task为后缀的文件
     * @param webHungerConfig config
     * @return 所有以task为后缀的文件
     */
    private File[] getTaskFiles(WebHungerConfig webHungerConfig) {
        String taskDir = webHungerConfig.getConfMap().getOrDefault("task.dir", DEFAULT_TASK_PATH);
        File taskDirFile = new File(taskDir);

        File[] taskFiles = taskDirFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".task");
            }
        });

        if (taskFiles == null || taskFiles.length == 0) {
            logger.error("未找到task文件......");
            return new File[]{};
        }
        return taskFiles;
    }

    private Date transferDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
