package me.shenchao.webhunger.exception;

/**
 * 配置文件解析异常
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ConfigParseException extends RuntimeException {

    public ConfigParseException(String msg) {
        super(msg);
    }
}
