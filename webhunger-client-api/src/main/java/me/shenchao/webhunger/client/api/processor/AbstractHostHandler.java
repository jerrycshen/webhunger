package me.shenchao.webhunger.client.api.processor;

import me.shenchao.webhunger.entity.Host;

/**
 * 用于对整个站点全局的处理，注意与{@link AbstractPageHandler} 区别，<br>
 *
 *     每一个基础该类的处理器，必须有一个无参构造函数
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class AbstractHostHandler {

    /**
     * 持有后继责任对象
     */
    private AbstractHostHandler successor;

    /**
     * 业务处理方法
     * @param host page
     */
    protected abstract void handleRequest(Host host);

    public final void handle(Host host) {
        handleRequest(host);
        if (getSuccessor() != null) {
            getSuccessor().handle(host);
        }
    }

    public AbstractHostHandler getSuccessor() {
        return successor;
    }

    public void setSuccessor(AbstractHostHandler successor) {
        this.successor = successor;
    }
}

