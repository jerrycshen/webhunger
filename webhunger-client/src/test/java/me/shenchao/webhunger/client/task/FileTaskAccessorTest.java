package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created on 2017-11-21
 *
 * @author Jerry Shen
 */
public class FileTaskAccessorTest {

    private WebHungerConfig webHungerConfig;

    private FileTaskAccessor fileTaskLoader = new FileTaskAccessor();


    @Before
    public void setUp() throws IOException, NoSuchMethodException {
        webHungerConfig = new WebHungerConfig();
        webHungerConfig.parse(FileTaskAccessorTest.class.getClassLoader().getResourceAsStream("webhunger.conf"));
    }

    @Test
    public void loadTasks() {
        Map<String, Task> taskMap = fileTaskLoader.loadTasks(webHungerConfig);
        Assert.assertEquals(1, taskMap.size());
    }

}
