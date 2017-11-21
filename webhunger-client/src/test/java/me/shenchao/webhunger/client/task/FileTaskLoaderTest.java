package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created on 2017-11-21
 *
 * @author Jerry Shen
 */
public class FileTaskLoaderTest {

    private WebHungerConfig webHungerConfig;

    private FileTaskLoader fileTaskLoader = new FileTaskLoader();

    private Method getTaskFiles;

    private Method parseTask;

    @Before
    public void setUp() throws IOException, NoSuchMethodException {
        webHungerConfig = new WebHungerConfig();
        webHungerConfig.parse(FileTaskLoaderTest.class.getClassLoader().getResourceAsStream("webhunger.conf"));

        getTaskFiles = FileTaskLoader.class.getDeclaredMethod("getTaskFiles", WebHungerConfig.class);
        getTaskFiles.setAccessible(true);

        parseTask = FileTaskLoader.class.getDeclaredMethod("parseTask", File.class);
        parseTask.setAccessible(true);
    }

    @Test
    public void getTaskFiles() throws InvocationTargetException, IllegalAccessException {
        File[] taskFiles = (File[]) getTaskFiles.invoke(fileTaskLoader, webHungerConfig);
        Assert.assertEquals(1,taskFiles.length);
    }

    @Test
    public void parseTask() throws InvocationTargetException, IllegalAccessException {
        File[] taskFile = (File[]) getTaskFiles.invoke(fileTaskLoader, webHungerConfig);
        Task task = (Task) parseTask.invoke(fileTaskLoader, taskFile[0]);
        Assert.assertEquals("Jerry Shen", task.getAuthor());
        Assert.assertEquals(2, task.getHosts().size());
    }

}
