package me.shenchao.webhunger.util.common;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemUtils {

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

    public static String getHostName() {
        StringBuilder hostName = new StringBuilder();
        try {
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress().toString();
            String name = address.getHostName().toString();
            hostName.append(name).append("@").append(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostName.append("unknown");
        }
        return hostName.toString();
    }

}
