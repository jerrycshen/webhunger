package me.shenchao.webhunger.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created on 2017-11-21
 *
 * @author Jerry Shen
 */
public class FileUtilTest {

    @Test
    public void getFileName() throws Exception {
        String fileName = "D:\\IdeaIU\\IdeaProjects\\webhunger\\conf\\webhunger.conf";
        Assert.assertEquals("webhunger", FileUtil.getFileName(new File(fileName)));
    }
}
