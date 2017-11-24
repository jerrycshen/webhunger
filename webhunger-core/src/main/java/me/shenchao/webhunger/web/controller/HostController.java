package me.shenchao.webhunger.web.controller;

import com.alibaba.fastjson.JSONObject;
import me.shenchao.webhunger.core.controller.ControllerFactory;
import me.shenchao.webhunger.core.controller.MasterController;
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

    @RequestMapping(value = "/task/{task_id}/host/list", method = RequestMethod.GET)
    public String viewHostTable(@PathVariable("task_id") String task_id, Model model) {
        model.addAttribute("task_id", task_id);
        return "host/view_host.jsp";
    }

    @RequestMapping(value = "/task/{task_id}/host/list", method = RequestMethod.POST ,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String viewHostData(@PathVariable("task_id") String task_id) {
        List<Host> hosts = masterController.getHostsByTaskId(task_id);
        JSONObject result = new JSONObject();
        result.put("data", hosts);
        return result.toJSONString();
    }

}
