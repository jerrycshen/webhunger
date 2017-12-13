package me.shenchao.webhunger.util.classloader;

import me.shenchao.webhunger.util.common.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 专门用于加载第三方类的类加载器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ThirdPartyClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(ThirdPartyClassLoader.class);

    /**
     * @param jarDir 第三方jar路径
     * @param className 全类名
     * @param clazz class类型
     */
    public static <T> T loadClass(String jarDir, String className, Class<T> clazz) {
        return loadClass(jarDir, className, clazz, null);
    }

    private static <T> T loadClass(String jarDir, String className, Class<T> clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = getURLClassLoader(jarDir);
        }
        T t = null;
        try {
            Class<T> aClass = (Class<T>) classLoader.loadClass(className);
            t = aClass.newInstance();
            logger.debug("加载类：{} 成功", className);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("加载类：{} 失败", className);
        }
        return t;
    }

    public static <T> List<T> loadClasses(String jarDir, List<String> classNames, Class<T> clazz) {
        ClassLoader classLoader = getURLClassLoader(jarDir);
        List<T> list = new ArrayList<>(classNames.size());
        for (String className : classNames) {
            T t = loadClass(jarDir, className, clazz, classLoader);
            if (t != null) {
                list.add(t);
            }
        }
        return list;
    }

    private static URLClassLoader getURLClassLoader(String jarDir) {
        List<URL> urls = new ArrayList<>();
        for (File file : FileUtils.getAllSuffixFilesInCurrentDir(jarDir, "jar")) {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        URL[] u = new URL[urls.size()];
        urls.toArray(u);
        return new URLClassLoader(u, ThirdPartyClassLoader.class.getClassLoader());
    }
}
