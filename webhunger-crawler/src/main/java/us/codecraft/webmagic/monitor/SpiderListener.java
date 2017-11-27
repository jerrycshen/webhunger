package us.codecraft.webmagic.monitor;

import us.codecraft.webmagic.Request;

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
