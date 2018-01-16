package me.shenchao.webhunger.client.api.processor;

import me.shenchao.webhunger.dto.PageDTO;

/**
 * 抽象页面处理类，不纯的职责链模式实现<br>
 *
 *     每一个基础该类的处理器，必须有一个无参构造函数
 *
 * @author Jerry Shen
 * @since 3.0
 */
public abstract class AbstractPageHandler {

    /**
     * 持有后继责任对象
     */
    private AbstractPageHandler successor;

    /**
     * 业务处理方法
     * @param page page
     */
    public abstract void handRequest(PageDTO page);

    public final void handle(PageDTO page) {
        handRequest(page);
        // 判断该页面是否无效，是否需要被继续处理
        if (page.isSkip()) {
            return;
        }
        if (getSuccessor() != null) {
            getSuccessor().handle(page);
        }
    }

    private AbstractPageHandler getSuccessor() {
        return successor;
    }

    /**
     * 设置后继的责任对象
     * @param successor 后继
     */
    public void setSuccessor(AbstractPageHandler successor) {
        this.successor = successor;
    }

}
