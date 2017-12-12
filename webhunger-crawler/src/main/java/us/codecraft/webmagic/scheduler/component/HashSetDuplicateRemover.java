package us.codecraft.webmagic.scheduler.component;

import com.google.common.collect.Sets;
import me.shenchao.webhunger.entity.webmagic.Request;
import us.codecraft.webmagic.LifeCycle;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author code4crafer@gmail.com
 */
public class HashSetDuplicateRemover implements DuplicateRemover {

    private Set<String> urls = Sets.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(Request request, LifeCycle task) {
        return !urls.add(getUrl(request));  // 如果集合中尚未存在此URL，那么返回false
    }

    protected String getUrl(Request request) {
        return request.getUrl();
    }

    @Override
    public void resetDuplicateCheck(LifeCycle task) {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount(LifeCycle task) {
        return urls.size();
    }
}
