package me.shenchao.webhunger.core;

import me.shenchao.webhunger.core.config.CoreConfig;
import me.shenchao.webhunger.exception.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core Control module bootstrap
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CoreBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(CoreBootstrap.class);

    private static final String BOOTSTRAP_CONFIG = "core.conf";

    private CoreConfig coreConfig;

    public void parseCoreConfig() {
        coreConfig = new CoreConfig();
        try {
            coreConfig.parse(BOOTSTRAP_CONFIG);
        } catch (ConfigException e) {
            logger.error(e.toString());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        CoreBootstrap bootstrap = new CoreBootstrap();
        bootstrap.parseCoreConfig();
    }
}
