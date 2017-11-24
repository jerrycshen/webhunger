package me.shenchao.webhunger.web;

import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.util.SystemUtil;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * start Jetty
 * @author Jerry Shen
 * @since 0.1
 */
public class WebConsoleStarter {

    private static final String DEFAULT_WEBAPP_PATH;

    private static final String PORT = "5572";

    private static final String CONTEXT_PATH = "/webhunger";

    static {
        DEFAULT_WEBAPP_PATH = SystemUtil.getWebHungerHomeDir() + "/webhunger-core/src/main/webapp";
    }

    public void startServer(int port, String context) throws Exception {
        // 创建Server
        Server server = new Server();
        // 设置在JVM退出时关闭Jetty
        server.setStopAtShutdown(true);
        // webContext
        WebAppContext webContext = new WebAppContext(DEFAULT_WEBAPP_PATH, context);
        webContext.setServer(server);
        // 设置webapp的位置
        webContext.setResourceBase(DEFAULT_WEBAPP_PATH);

        server.setHandler(webContext);
        // 这是http的连接器
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        // 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
//        connector.setReuseAddress(false);
        server.setConnectors(new Connector[] { connector });

        server.start();
    }

    public void startServer(WebHungerConfig webHungerConfig) throws Exception {
        int port = Integer.parseInt(webHungerConfig.getConfMap().getOrDefault("port", PORT));
        String contextPath = webHungerConfig.getConfMap().getOrDefault("contextPath", CONTEXT_PATH);
        startServer(port, contextPath);
    }
}
