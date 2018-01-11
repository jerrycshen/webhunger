package me.shenchao.webhunger.web.controller;

import com.alibaba.fastjson.JSONObject;
import me.shenchao.webhunger.control.controller.ControllerFactory;
import me.shenchao.webhunger.control.controller.DistributedController;
import me.shenchao.webhunger.control.controller.MasterController;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostState;
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
        model.addAttribute("taskName", masterController.getTaskById(taskId).getTaskName());
        model.addAttribute("taskId", taskId);
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

    @RequestMapping(value = "/host/{hostId}/config/show", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String getHostConfig(@PathVariable String hostId) {
        JSONObject result = new JSONObject();
        result.put("data", masterController.getHostById(hostId));
        return result.toJSONString();
    }

    @RequestMapping(value = "/host/{hostId}/report", method = RequestMethod.GET)
    public String reportCrawler(@PathVariable String hostId, Model model) {
        boolean isDistributed = masterController instanceof DistributedController;
        model.addAttribute("hostId", hostId);
        model.addAttribute("isDistributed", isDistributed);
        Host host = masterController.getHostById(hostId);
        if (host.getState() == HostState.Completed.getState()) {
            return "host/completed_report.jsp";
        } else {
            // 根据不同爬取模式，设置不同的站点进度刷新间隔
            if (isDistributed) {
                model.addAttribute("flushInterval", 6000);
            } else {
                model.addAttribute("flushInterval", 3000);
            }
            return "host/running_report.jsp";
        }
    }

    @RequestMapping(value = "/host/{hostId}/progress", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String pullProgress(@PathVariable String hostId) {
        JSONObject result = new JSONObject();
        HostCrawlingSnapshotDTO snapshot = masterController.getCurrentCrawlingSnapshot(hostId);
        result.put("data", snapshot);
        return result.toJSONString();
    }
}
