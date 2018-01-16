package me.shenchao.webhunger.web.controller;

import com.alibaba.fastjson.JSONObject;
import me.shenchao.webhunger.control.controller.ControllerFactory;
import me.shenchao.webhunger.control.controller.DistributedController;
import me.shenchao.webhunger.control.controller.MasterController;
import me.shenchao.webhunger.entity.DistributedNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面处理节点控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
@Controller
public class ProcessorController {

    private MasterController masterController = ControllerFactory.getController();

    @RequestMapping(value = "/processor/list", method = RequestMethod.GET)
    public String viewProcessorTable() {
        return "processor/processor_view.jsp";
    }

    @RequestMapping(value = "/processor/list", method = RequestMethod.POST)
    @ResponseBody
    public String viewProcessorData() {
        List<DistributedNode> processors = new ArrayList<>();
        if (masterController.isDistributed()) {
            DistributedController distributedController = (DistributedController) masterController;
            processors = distributedController.getOnlineProcessors();
        }
        JSONObject result = new JSONObject();
        result.put("data", processors);
        return result.toJSONString();
    }

    @RequestMapping(value = "/processor/{processorIP}/run", method = RequestMethod.POST)
    @ResponseBody
    public String runProcessor(@PathVariable("processorIP") String processorIP) {
        JSONObject result =  new JSONObject();
        result.put("res_code", 1);
        if (masterController.isDistributed()) {
            DistributedController distributedController = (DistributedController) masterController;
            distributedController.runProcessor(processorIP);
            result.put("res_code", 0);
        }
        return result.toJSONString();
    }
}
