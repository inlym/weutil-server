package com.fribuddy.user.exception;

import com.fribuddy.common.exception.BaseException;

/**
 * 账号已注销异常
 *
 * <h2>异常说明
 * <p>当用户账号已注销但仍尝试访问系统时抛出此异常。
 * <p>通常在用户登录、权限验证或需要有效用户账号的操作中触发。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class AccountCancelledException extends BaseException {

    /**
     * 构造函数
     *
     * <h3>使用场景
     * <p>创建账号已注销异常实例
     *
     * @param message 详细错误消息
     */
    public AccountCancelledException(String message) {
        super(message);
    }
}
