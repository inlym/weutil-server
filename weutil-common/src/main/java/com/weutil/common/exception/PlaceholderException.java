package com.weutil.common.exception;

/**
 * 占位异常
 *
 * <h2>主要用途
 * <p>某处分支代码大概率不会出错，为了结构完整，使用当前异常抛出。（检测到实际发生后，替换为正常的 Exception）
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 **/
public class PlaceholderException extends BaseException {

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public PlaceholderException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public PlaceholderException(String message, Throwable cause) {
        super(message, cause);
    }
}