package us.codecraft.webmagic.monitor;

import me.shenchao.webhunger.entity.webmagic.Request;

/**
 * Listener of Spider
 *
 * @since 1.2
 * @author jerry
 */
public interface SpiderListener {

    void onSuccess(Request request);

    void onError(Request request);

    void onCompleted();
}
