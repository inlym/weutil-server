package com.fribuddy.user.config;

import com.fribuddy.user.exception.AccountCancelledException;
import com.fribuddy.user.exception.AccountLockedException;
import com.fribuddy.user.exception.UserNotFoundException;
import com.fribuddy.common.model.response.ErrorInfo;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 用户模块异常处理器
 *
 * <h2>类说明
 * <p>统一捕获用户模块中的业务异常，并将异常信息转换为标准的错误响应。
 * <p>通过 @RestControllerAdvice 注解实现模块级异常拦截，优先于全局异常处理器执行。
 * <p>使用较高优先级，确保用户模块的异常优先被本处理器处理。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

    /**
     * 处理用户未找到异常
     *
     * <h3>方法说明
     * <p>当通过用户 ID 查询用户时未找到对应的记录时触发。
     *
     * @param e 用户未找到异常
     * @return 错误响应，错误码为 ACCOUNT_ABNORMAL
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorInfo handleUserNotFound(UserNotFoundException e) {
        return new ErrorInfo("ACCOUNT_ABNORMAL", "response.user.account_abnormal");
    }

    /**
     * 处理账号已注销异常
     *
     * <h3>方法说明
     * <p>当用户账号已注销但仍尝试访问系统时触发。
     *
     * @param e 账号已注销异常
     * @return 错误响应，错误码为 ACCOUNT_CANCELLED
     */
    @ExceptionHandler(AccountCancelledException.class)
    public ErrorInfo handleAccountCancelled(AccountCancelledException e) {
        return new ErrorInfo("ACCOUNT_CANCELLED", "response.user.account_cancelled");
    }

    /**
     * 处理账号已锁定异常
     *
     * <h3>方法说明
     * <p>当用户账号已被锁定但仍尝试访问系统时触发。
     *
     * @param e 账号已锁定异常
     * @return 错误响应，错误码为 ACCOUNT_LOCKED
     */
    @ExceptionHandler(AccountLockedException.class)
    public ErrorInfo handleAccountLocked(AccountLockedException e) {
        return new ErrorInfo("ACCOUNT_LOCKED", "response.user.account_locked");
    }
}
