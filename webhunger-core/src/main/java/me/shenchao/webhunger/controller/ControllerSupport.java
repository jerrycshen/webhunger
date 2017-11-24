package me.shenchao.webhunger.controller;

import me.shenchao.webhunger.client.api.TaskAccessor;
import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 控制器辅助类
 *
 * @author Jerry Shen
 * @since 0.1
 */
class ControllerSupport {

    private static final Logger logger = LoggerFactory.getLogger(ControllerSupport.class);

    /**
     * 加载用户自定义的Jar
     */
    TaskAccessor getTaskLoader(WebHungerConfig webHungerConfig) {
        String taskAccessorJarDir = webHungerConfig.getConfMap().get("taskAccessorJarDir");
        String taskAccessorClass = webHungerConfig.getConfMap().get("taskAccessorClass");

        List<URL> urls = new ArrayList<>();
        for (File file : FileUtil.getAllSuffixFilesInCurrentDir(taskAccessorJarDir, ".jar")) {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        URL[] u = new URL[urls.size()];
        urls.toArray(u);
        ClassLoader classLoader = new URLClassLoader(u, getClass().getClassLoader());
        TaskAccessor taskAccessor = null;
        try {
            Class<TaskAccessor> clazz = (Class<TaskAccessor>) classLoader.loadClass(taskAccessorClass);
            taskAccessor = clazz.newInstance();
            return taskAccessor;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取任务数据访问器失败，程序退出......", e);
            System.exit(1);
        }
        assert taskAccessor != null;
        return taskAccessor;
    }
}
