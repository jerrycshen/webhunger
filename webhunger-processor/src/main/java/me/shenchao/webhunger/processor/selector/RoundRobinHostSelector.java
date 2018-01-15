package me.shenchao.webhunger.processor.selector;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.processor.listener.HostPageNumListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Round-Robin算法的站点选择器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class RoundRobinHostSelector implements HostSelector {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public Host select(HostPageNumListener hostListener) {
        Host nextHost;
        if ((nextHost = nextHost()) == null) {
            return null;
        }
        while (true) {
            if (nextHost == null) {
                return null;
            }
            int leftPagesNum = hostListener.getLeftPagesNum(nextHost.getHostId());
            if (leftPagesNum > 0) {
                return nextHost;
            } else {
                // TODO check
            }
        }
    }

    private Host nextHost() {
        return null;
    }
}
