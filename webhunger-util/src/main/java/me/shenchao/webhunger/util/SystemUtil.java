package me.shenchao.webhunger.util;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SystemUtil {

    public static String getWebHungerHomeDir() {
        String homeDir = System.getProperty("webhunger.home");
        if (homeDir == null) {
            URL url = SystemUtil.class.getClassLoader().getResource("me/shenchao/webhunger/core/CoreBootstrap.class");
            if (url != null) {
                try {
                    JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
                    url = jarConnection.getJarFileURL();
                    URI baseURI = new URI(url.toString()).resolve("..");
                    homeDir = baseURI.toString();
                    System.out.println(homeDir);
                    System.setProperty("webhunger.home", homeDir);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
