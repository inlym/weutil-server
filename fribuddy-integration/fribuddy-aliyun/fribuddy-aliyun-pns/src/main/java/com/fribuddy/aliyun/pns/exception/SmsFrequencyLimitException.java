package com.fribuddy.aliyun.pns.exception;

import com.fribuddy.common.exception.BaseException;

/**
 * 短信发送频次超出限制异常
 *
 * <h2>异常说明
 * <p>当短信认证服务判定发送频次超出限制时抛出此异常。
 * <p>对应阿里云响应业务码 biz.FREQUENCY。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
public class SmsFrequencyLimitException extends BaseException {

    /**
     * 构造方法
     *
     * <h3>使用场景
     * <p>创建短信发送频次超出限制异常实例
     *
     * @param message 详细错误消息
     */
    public SmsFrequencyLimitException(String message) {
        super(message);
    }
}
