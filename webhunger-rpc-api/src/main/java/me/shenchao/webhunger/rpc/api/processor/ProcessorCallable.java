package me.shenchao.webhunger.rpc.api.processor;

/**
 * RPC接口，负责页面处理操作接口
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface ProcessorCallable {

    /**
     * 使页面处理节点状态从Ready转变为Running状态。之后会进行页面处理
     */
    void run();
}
