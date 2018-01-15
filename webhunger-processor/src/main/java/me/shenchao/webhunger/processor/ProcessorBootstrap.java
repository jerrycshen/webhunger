package me.shenchao.webhunger.processor;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import me.shenchao.webhunger.config.ProcessorConfig;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.rpc.api.processor.ProcessorCallable;
import me.shenchao.webhunger.util.common.SystemUtils;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 处理模块启动类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ProcessorBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorBootstrap.class);

    private static final String CONF_NAME = "webhunger.conf";

    private ProcessorConfig processorConfig;

    private ProcessorCallable processorCallable;

    /**
     * 启动处理节点
     */
    public void start() {
        logger.info("页面处理节点正在启动......");
        // 解析配置
        parseProcessorConfig();
        Processor processor = Processor.create();
        if (!processorConfig.isDistributed()) {
            throw new IllegalStateException("单机模式无需启动页面处理模块......");
        }
        // 启动zookeeper,注册本页面处理节点
        ZooKeeper zooKeeper = initZookeeper();
        // 启动dubbo，暴露接口与控制器RPC通信
        initDubbo();
        processor.setThreadNum(5);
        processor.runAsync();
        logger.info("页面处理节点启动成功，等待调度运行......");

    }

    private ZooKeeper initZookeeper() {
        ZooKeeper zooKeeper = ZookeeperUtils.getZKConnection(processorConfig.getZkAddress());
        try {
            zooKeeper.create(ZookeeperPathConsts.getProcessorNodePath(), "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            logger.info("Zookeeper连接成功......");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("启动失败：未能向Zookeeper注册本页面处理节点");
        }
        return zooKeeper;
    }

    private void initDubbo() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("Processor");

        // 服务提供协议配置
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(20880);
        protocolConfig.setThreads(1);

        // 由于使用直连方式，所以不使用注册中心
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("N/A");

        // 服务配置
        ServiceConfig<ProcessorCallable> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setProtocol(protocolConfig);
        serviceConfig.setInterface(ProcessorCallable.class);
        serviceConfig.setRef(processorCallable);
        serviceConfig.setVersion("0.1");

        // 暴露服务
        serviceConfig.export();
        logger.info("Dubbo注册成功......");
    }

    private void parseProcessorConfig() {
        processorConfig = new ProcessorConfig();
        try {
            processorConfig.parse(SystemUtils.getWebHungerConfigDir() + File.separator + CONF_NAME);
        } catch (IOException e) {
            logger.error("页面处理模块配置文件读取失败，程序退出......", e);
            System.exit(1);
        }
        // log config info
        logger.info("配置解析完成，使用如下参数启动页面处理程序：");
        logger.info("{}", processorConfig);
    }

    public static void main(String[] args) {
        new ProcessorBootstrap().start();
    }
}
