package me.shenchao.webhunger.util;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

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

    /**
     * 获取当前目录下所有以suffix为后缀的文件，此方法会递归遍历
     * @param currentDir 当前目录
     * @param suffix 后缀名
     */
    public static List<File> getAllSuffixFilesInCurrentDir(String currentDir, String suffix) {
        return Files.fileTreeTraverser().breadthFirstTraversal(new File(currentDir)).filter(new Predicate<File>() {
            @Override
            public boolean apply(@Nullable File file) {
                return file.getName().endsWith(suffix);
            }
        }).toList();
    }
}
