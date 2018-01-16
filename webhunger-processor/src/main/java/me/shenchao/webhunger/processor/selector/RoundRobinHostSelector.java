package me.shenchao.webhunger.processor.selector;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.processor.dominate.BaseHostDominate;
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

    private BaseHostDominate hostDominate;

    public RoundRobinHostSelector(BaseHostDominate hostDominate) {
        this.hostDominate = hostDominate;
    }

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
                // 如果该站点对应的待处理页面数量为0，检查站点是否处理完毕
                hostDominate.checkProcessedCompleted(nextHost.getHostId(), hostListener);
                nextHost = nextHost();
            }
        }
    }

    private Host nextHost() {
        int processingHostNum = hostDominate.getHostList().size();
        if (processingHostNum == 0) {
            return null;
        }
        int pos = index.incrementAndGet() % processingHostNum;
        return hostDominate.getHostList().get(pos);
    }
}
