package me.shenchao.webhunger.client.control;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.exception.ConfigParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created on 2017-11-21
 *
 * @author Jerry Shen
 */
public class FileTaskAccessorTest {

    private ControlConfig controlConfig;

    private FileTaskAccessor fileTaskLoader = new FileTaskAccessor();

    static {
        System.setProperty("webhunger.home", "/Users/jerry/IdeaProjects/webhunger");
    }

    @Before
    public void setUp() throws IOException, ConfigParseException {
        controlConfig = new ControlConfig();
        controlConfig.parse(FileTaskAccessorTest.class.getClassLoader().getResourceAsStream("webhunger.conf"));
    }

    @Test
    public void loadTasks() {
        List<Task> tasks = fileTaskLoader.loadTasks();
        Assert.assertEquals(1, tasks.size());
    }

}
