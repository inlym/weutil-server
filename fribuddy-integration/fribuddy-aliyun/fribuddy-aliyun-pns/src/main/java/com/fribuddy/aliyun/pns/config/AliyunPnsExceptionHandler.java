package com.fribuddy.aliyun.pns.config;

import com.fribuddy.aliyun.pns.exception.MobileNumberIllegalException;
import com.fribuddy.aliyun.pns.exception.SmsFrequencyLimitException;
import com.fribuddy.aliyun.pns.exception.SmsSendFailureException;
import com.fribuddy.common.model.response.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 号码认证模块异常处理器
 *
 * <h2>类说明
 * <p>统一捕获号码认证模块中的业务异常，并将异常信息转换为标准的错误响应。
 * <p>通过 @RestControllerAdvice 注解实现模块级异常拦截，优先于全局异常处理器执行。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AliyunPnsExceptionHandler {

    /**
     * 处理手机号格式异常
     *
     * <h3>方法说明
     * <p>当短信认证服务判定手机号格式不合法时触发。
     *
     * @param e 手机号格式异常
     * @return 错误响应，错误码为 MOBILE_NUMBER_ILLEGAL
     */
    @ExceptionHandler(MobileNumberIllegalException.class)
    public ErrorInfo handleMobileNumberIllegal(MobileNumberIllegalException e) {
        return new ErrorInfo("MOBILE_NUMBER_ILLEGAL", "response.sms.mobile_number_illegal");
    }

    /**
     * 处理短信发送频次超出限制异常
     *
     * <h3>方法说明
     * <p>当短信认证的发送频次超出限制时触发。
     *
     * @param e 短信发送频次超出限制异常
     * @return 错误响应，错误码为 SMS_FREQUENCY_LIMIT
     */
    @ExceptionHandler(SmsFrequencyLimitException.class)
    public ErrorInfo handleSmsFrequencyLimit(SmsFrequencyLimitException e) {
        return new ErrorInfo("SMS_FREQUENCY_LIMIT", "response.sms.frequency_limit");
    }

    /**
     * 处理短信发送失败异常
     *
     * <h3>方法说明
     * <p>作为短信发送的兜底异常，其余发送失败场景均触发，属于系统级异常，记录 ERROR 日志。
     *
     * @param e 短信发送失败异常
     * @return 错误响应，错误码为 SMS_SEND_FAILURE
     */
    @ExceptionHandler(SmsSendFailureException.class)
    public ErrorInfo handleSmsSendFailure(SmsSendFailureException e) {
        log.error("短信发送失败", e);
        return new ErrorInfo("SMS_SEND_FAILURE", "response.sms.send_failure");
    }
}
