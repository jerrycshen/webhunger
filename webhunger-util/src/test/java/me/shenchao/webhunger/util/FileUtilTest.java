package me.shenchao.webhunger.util;

import me.shenchao.webhunger.util.common.FileUtil;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created on 2017-11-21
 *
 * @author Jerry Shen
 */
public class FileUtilTest {

    @Test
    public void getAllSuffixFilesInCurrentDir() {
        String dir = "/Users/jerry/IdeaProjects/webhunger";
        List<File> files = FileUtil.getAllSuffixFilesInCurrentDir(dir, ".java");
        System.out.println(files.size());
        for (File file : files) {
            System.out.println(file.getName());
        }
    }
}
