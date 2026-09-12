package com.fribuddy.common.exception;

/**
 * 意料之外异常
 *
 * <h2>异常说明
 * <p>用于分支判断中，在认为已包含所有情况处理后，若不匹配已有情况则抛出此异常。
 *
 * <h2>主要用途
 * <p>出现了这个异常表示出现了之前未考虑到的情况，利于在开发阶段提早发现错误。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class UnpredictableException extends BaseException {

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public UnpredictableException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public UnpredictableException(String message, Throwable cause) {
        super(message, cause);
    }
}