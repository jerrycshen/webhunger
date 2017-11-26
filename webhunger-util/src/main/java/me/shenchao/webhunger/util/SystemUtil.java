package me.shenchao.webhunger.util;

import java.io.File;

public class SystemUtil {

    public static String getWebHungerHomeDir() {
        String homeDir = System.getProperty("webhunger.home");
        if (homeDir == null) {
            throw new RuntimeException("未指定程序根目录，程序退出......");
        }
        return homeDir;
    }

    public static String getWebHungerConfigDir() {
        String configDir = System.getProperty("webhunger.conf");
        if (configDir == null) {
            return getWebHungerHomeDir() + File.separator + "conf";
        }
        return configDir;
    }

    public static String getWebHungerDefaultDir() {
        String userDir = System.getProperty("webhunger.default");
        if (userDir == null) {
            return getWebHungerHomeDir() + File.separator + "default";
        }
        return userDir;
    }

}
