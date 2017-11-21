package me.shenchao.webhunger.util;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SystemUtil {

    public static String getWebHungerHomeDir() {
        String homeDir = System.getProperty("webhunger.home");
        if (homeDir == null) {
            throw new RuntimeException("未指定项目路径，程序退出......");
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

    public static String getWebHungerUserDir() {
        String userDir = System.getProperty("webhunger.user");
        if (userDir == null) {
            return getWebHungerHomeDir() + File.separator + "user";
        }
        return userDir;
    }

    /**
     * 使用Base64 压缩UUID 生成唯一ID
     */
    public static String generateRandomId() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Base64.encodeBase64URLSafeString(bb.array());
    }
}
