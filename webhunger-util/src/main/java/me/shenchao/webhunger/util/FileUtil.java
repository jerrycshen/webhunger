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
}
