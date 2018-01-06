package me.shenchao.webhunger.web.controller;

import com.alibaba.fastjson.JSONObject;
import me.shenchao.webhunger.control.controller.ControllerFactory;
import me.shenchao.webhunger.control.controller.DistributedController;
import me.shenchao.webhunger.control.controller.MasterController;
import me.shenchao.webhunger.entity.Crawler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫节点控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
@Controller
public class CrawlerController {

    private MasterController masterController = ControllerFactory.getController();

    @RequestMapping(value = "/crawler/list", method = RequestMethod.GET)
    public String viewCrawlerTable() {
        return "crawler/crawler_view.jsp";
    }

    @RequestMapping(value = "/crawler/list", method = RequestMethod.POST)
    @ResponseBody
    public String viewCrawlerData() {
        List<Crawler> crawlers = new ArrayList<>();
        if (masterController instanceof DistributedController) {
            DistributedController distributedController = (DistributedController) masterController;
            crawlers = distributedController.getOnlineCrawlers();
        }
        JSONObject result = new JSONObject();
        result.put("data", crawlers);
        return result.toJSONString();
    }

    @RequestMapping(value = "/crawler/{crawlerIP}/run", method = RequestMethod.POST)
    @ResponseBody
    public String runCrawler(@PathVariable("crawlerIP") String crawlerIP) {
        JSONObject result =  new JSONObject();
        result.put("res_code", 1);
        if (masterController instanceof DistributedController) {
            DistributedController distributedController = (DistributedController) masterController;
            distributedController.run(crawlerIP);
            result.put("res_code", 0);
        }
        return result.toJSONString();
    }
}
