package me.shenchao.webhunger.core.util;

import java.io.File;

public class SystemUtil {

    public static String getWebHungerHome() {
        String homeDir = System.getProperty("webhunger.home");
        if (homeDir == null) {
            throw new RuntimeException("未指定项目路径，程序退出......");
        }
        return homeDir;
    }

    public static String getWebHungerConfig() {
        String configDir = System.getProperty("webhunger.conf");
        if (configDir == null) {
            return getWebHungerHome() + File.separator + "conf";
        }
        return configDir;
    }
}
