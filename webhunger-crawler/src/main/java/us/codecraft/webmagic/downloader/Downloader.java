package us.codecraft.webmagic.downloader;

import me.shenchao.webhunger.entity.webmagic.Page;
import me.shenchao.webhunger.entity.webmagic.Request;
import us.codecraft.webmagic.LifeCycle;

/**
 * Downloader is the part that downloads web pages and store in Page object. <br>
 * Downloader has {@link #setThread(int)} method because downloader is always the bottleneck（瓶颈） of a crawler,
 * there are always some mechanisms such as pooling in downloader, and pool size is related to runnable numbers.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Downloader {

    /**
     * Downloads web pages and store in Page object.
     *
     * @param request request
     * @param lifeCycle lifeCycle
     * @return page
     */
    Page download(Request request, LifeCycle lifeCycle);

    /**
     * Tell the downloader how many threads the spider used.
     * @param threadNum number of threads
     */
    void setThread(int threadNum);
}
