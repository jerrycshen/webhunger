package us.codecraft.webmagic.scheduler;

import org.apache.http.annotation.ThreadSafe;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.LifeCycle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Basic Scheduler implementation.<br>
 * Store urls to fetch in LinkedBlockingQueue and remove duplicate urls by HashMap.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class QueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

    private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

    @Override
    public void pushWhenNoDuplicate(Request request, LifeCycle task) {
        queue.add(request);
    }

    @Override
    public Request poll(LifeCycle task) {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount(LifeCycle task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(LifeCycle task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
