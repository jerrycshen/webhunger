package us.codecraft.webmagic.pipeline;

import me.shenchao.webhunger.entity.webmagic.ResultItems;
import us.codecraft.webmagic.LifeCycle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 * @since 0.4.0
 */
public class ResultItemsCollectorPipeline implements CollectorPipeline<ResultItems> {

    private List<ResultItems> collector = new ArrayList<ResultItems>();

    @Override
    public List<ResultItems> getCollected() {
        return collector;
    }

    @Override
    public void process(ResultItems resultItems, LifeCycle task) {
        collector.add(resultItems);
    }
}
