package me.shenchao.webhunger.crawler.pipeline;

import me.shenchao.webhunger.dto.PageDTO;
import me.shenchao.webhunger.processor.Processor;
import us.codecraft.webmagic.LifeCycle;
import me.shenchao.webhunger.entity.webmagic.ResultItems;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 单机爬取使用的页面处理器，在单机爬取中，在爬取线程中完成对页面的处理。这样设计的原因在于
 * 更简单控制爬取的进度，虽然这样做会影响爬起的效率
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class LocalPipeline implements Pipeline {

    private Processor processor = Processor.create();

    @Override
    public void process(ResultItems resultItems, LifeCycle lifeCycle) {
        PageDTO page = resultItems.get("page");
        processor.processPage(page, lifeCycle.getSites().get(page.getHostId()).getHost());
    }
}
