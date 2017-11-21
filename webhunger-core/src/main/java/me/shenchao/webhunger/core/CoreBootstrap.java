package me.shenchao.webhunger.core;

import me.shenchao.webhunger.client.api.TaskLoader;
import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.util.FileUtil;
import me.shenchao.webhunger.util.SystemUtil;
import me.shenchao.webhunger.web.WebConsoleStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core Control module bootstrap
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CoreBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(CoreBootstrap.class);

    private static final String CONF_NAME = "webhunger.conf";

    private WebHungerConfig webHungerConfig;

    /**
     * 每一批任务对应一个ClassLoader
     */
    private Map<String, ClassLoader> taskClassLoaderMap = new HashMap<>();

    private void parseCoreConfig() {
        webHungerConfig = new WebHungerConfig();
        try {
            webHungerConfig.parse(SystemUtil.getWebHungerConfigDir() + File.separator + CONF_NAME);
        } catch (Exception e) {
            logger.error("配置文件解析失败，程序退出......", e);
            System.exit(1);
        }
    }

    /**
     * 加载用户自定义的Jar
     */
    private TaskLoader getTaskLoader() {
        String taskLoaderJarDir = webHungerConfig.getConfMap().get("taskLoaderJarDir");
        String taskLoaderClass = webHungerConfig.getConfMap().get("taskLoaderClass");

        List<URL> urls = new ArrayList<>();
        for (File file : FileUtil.getAllSuffixFilesInCurrentDir(taskLoaderJarDir, ".jar")) {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        URL[] u = new URL[urls.size()];
        urls.toArray(u);
        ClassLoader classLoader = new URLClassLoader(u, getClass().getClassLoader());
        TaskLoader taskLoader = null;
        try {
            Class<TaskLoader> clazz = (Class<TaskLoader>) classLoader.loadClass(taskLoaderClass);
            taskLoader = clazz.newInstance();
            return taskLoader;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取任务数据加载器失败，程序退出......", e);
            System.exit(1);
        }
        assert taskLoader != null;
        return taskLoader;
    }

    private void start() {
        // 读取任务数据
        TaskLoader taskLoader = getTaskLoader();
        List<Task> tasks = taskLoader.loadTasks(webHungerConfig);

        // 启动web控制台
//        try {
//            new WebConsoleStarter().startServer(webHungerConfig);
//        } catch (Exception e) {
//            logger.error("Web控制台启动失败，程序退出......", e);
//            System.exit(1);
//        }
    }

    public static void main(String[] args) {
        CoreBootstrap bootstrap = new CoreBootstrap();
        bootstrap.parseCoreConfig();
        bootstrap.start();
    }
}
