package me.shenchao.webhunger.web.controller;

import com.alibaba.fastjson.JSONObject;
import me.shenchao.webhunger.control.controller.MasterController;
import me.shenchao.webhunger.control.controller.ControllerFactory;
import me.shenchao.webhunger.entity.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Jerry Shen
 * @since 0.1
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    private MasterController masterController = ControllerFactory.getController();

    /**
     * view task list
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String viewTaskTable() {
        return "task/task_view.jsp";
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String viewTaskData() {
        List<Task> tasks = masterController.getTasks();
        tasks.removeIf(task -> task.getState() == 0);
        JSONObject result = new JSONObject();
        result.put("data", tasks);
        return result.toJSONString();
    }
}
