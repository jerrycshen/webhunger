package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.util.SystemUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created on 2017-11-21
 *
 * @author Jerry Shen
 */
public class FileTaskLoaderTest {

    private WebHungerConfig webHungerConfig;

    private FileTaskLoader fileTaskLoader = new FileTaskLoader();

    private Method getTaskFiles;

    static {
        System.setProperty("webhunger.home", "D:\\IdeaIU\\IdeaProjects\\webhunger");
    }

    @Before
    public void setUp() throws IOException, NoSuchMethodException {
        webHungerConfig = new WebHungerConfig();
        webHungerConfig.parse(FileTaskLoaderTest.class.getClassLoader().getResourceAsStream("webhunger.conf"));

        getTaskFiles = FileTaskLoader.class.getDeclaredMethod("getTaskFiles", WebHungerConfig.class);
        getTaskFiles.setAccessible(true);
    }

    @Test
    public void getTaskFiles() throws InvocationTargetException, IllegalAccessException {
        File[] taskFiles = (File[]) getTaskFiles.invoke(fileTaskLoader, webHungerConfig);
        Assert.assertEquals(1,taskFiles.length);
    }

    @Test
    public void loadTasks() throws Exception {
        fileTaskLoader.loadTasks(webHungerConfig);
    }

    @Test
    public void test() {
        Host host1 = new Host();
        host1.setHostIndex("123");
        host1.setHostId("1");
        Host host2 = new Host();
        host2.setHostId("2");
        host2.setHostIndex("123");
        Set<Host> hosts = new HashSet<>();
        hosts.add(host1);
        hosts.add(host2);
        System.out.println(hosts.size());
    }

}
