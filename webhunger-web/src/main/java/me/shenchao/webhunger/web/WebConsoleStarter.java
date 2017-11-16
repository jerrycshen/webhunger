package me.shenchao.webhunger.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebConsoleStarter {

    private static final String DEFAULT_WEBAPP_PATH = "webhunger-core/src/main/webapp";

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
        connector.setReuseAddress(false);
        server.setConnectors(new Connector[] { connector });

        server.start();
        server.join();
    }

}
