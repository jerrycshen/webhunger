package me.shenchao.webhunger.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面处理器配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class HandlerConfig {

    private String handlerJarDir;

    /**
     * 会按照配置文件中定义的顺序进行页面处理
     */
    private List<String> handlerClassList = new ArrayList<>();

    public String getHandlerJarDir() {
        return handlerJarDir;
    }

    public void setHandlerJarDir(String handlerJarDir) {
        this.handlerJarDir = handlerJarDir;
    }

    public List<String> getHandlerClassList() {
        return handlerClassList;
    }

    public void addHandlerClass(String handlerClass) {
        handlerClassList.add(handlerClass);
    }
}
