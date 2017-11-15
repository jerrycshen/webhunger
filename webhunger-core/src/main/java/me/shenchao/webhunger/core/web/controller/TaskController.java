package me.shenchao.webhunger.core.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created on 2017-06-11
 *
 * @author Jerry Shen
 * @since 3.0
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    /**
     * view task list
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String viewTaskTable() {
        return "task/task_view.jsp";
    }

}
