package me.shenchao.webhunger.core.config;

import me.shenchao.webhunger.exception.ConfigException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CoreConfigTest {

    private CoreConfig coreConfig;

    @Before
    public void setUp() {
        coreConfig = new CoreConfig();
    }

    @Test(expected = ConfigException.class)
    public void testDirNotExist() throws ConfigException {
        coreConfig.parse("core_error.conf");
    }

    @Test
    public void testValue() throws ConfigException {
        coreConfig.parse("core.conf");
        Assert.assertTrue(coreConfig.iStandalone());
        Assert.assertEquals(5572, coreConfig.getPort());
    }

}
