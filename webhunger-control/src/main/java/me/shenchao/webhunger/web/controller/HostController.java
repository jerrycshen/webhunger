package me.shenchao.webhunger.web.controller;

import com.alibaba.fastjson.JSONObject;
import me.shenchao.webhunger.control.controller.ControllerFactory;
import me.shenchao.webhunger.control.controller.MasterController;
import me.shenchao.webhunger.entity.Host;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Jerry Shen
 * @since 0.1
 */
@Controller
public class HostController {

    private MasterController masterController = ControllerFactory.getController();

    @RequestMapping(value = "/task/{taskId}/host/list", method = RequestMethod.GET)
    public String viewHostTable(@PathVariable("taskId") String taskId, Model model) {
        model.addAttribute("task_id", taskId);
        return "host/view_host.jsp";
    }

    @RequestMapping(value = "/task/{taskId}/host/list", method = RequestMethod.POST ,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String viewHostData(@PathVariable("taskId") String taskId) {
        List<Host> hosts = masterController.getHostsByTaskId(taskId);
        JSONObject result = new JSONObject();
        result.put("data", hosts);
        return result.toJSONString();
    }

    /**
     * 启动爬虫
     *
     * @param hostId 站点id
     * @return msg
     */
    @RequestMapping(value = "/host/{hostId}/start", method = RequestMethod.POST)
    @ResponseBody
    public String startCrawler(@PathVariable String hostId) {
        masterController.start(hostId);
        return "success";
    }
}
