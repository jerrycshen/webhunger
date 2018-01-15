package me.shenchao.webhunger.client.exceptioin;

/**
 * 解析task文件异常
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class TaskParseException extends RuntimeException {

    public TaskParseException(String msg) {
        super(msg);
    }
}
