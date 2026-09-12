package com.fribuddy.aliyun.pns.exception;

import com.fribuddy.common.exception.BaseException;

/**
 * 短信发送失败异常
 *
 * <h2>异常说明
 * <p>短信发送流程的兜底异常，除手机号格式异常和发送频次超限外的所有失败场景均封装为此异常。
 * <p>包括接口调用失败、响应缺少验证码、其余未识别的响应业务码等。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
public class SmsSendFailureException extends BaseException {

    /**
     * 构造方法
     *
     * <h3>使用场景
     * <p>创建短信发送失败异常实例
     *
     * @param message 详细错误消息
     */
    public SmsSendFailureException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * <h3>使用场景
     * <p>创建携带原始异常的短信发送失败异常实例
     *
     * @param message 详细错误消息
     * @param cause   原始异常
     */
    public SmsSendFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
