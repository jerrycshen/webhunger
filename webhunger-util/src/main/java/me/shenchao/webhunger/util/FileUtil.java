package me.shenchao.webhunger.util;

import java.io.File;

/**
 * @author Jerry Shen
 * @since 0.1
 */
public class FileUtil {

    public static boolean validateFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * @param file file
     * @return 获得去掉后缀名的文件名称
     */
    public static String getFileName(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, dotIndex);
    }
}
