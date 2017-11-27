package us.codecraft.webmagic.downloader;

import us.codecraft.webmagic.Request;

/**
 * Base class of downloader with some common methods.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public abstract class AbstractDownloader implements Downloader {

    protected void onSuccess(Request request) {
    }

    protected void onError(Request request) {
    }

}
