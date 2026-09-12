package com.fribuddy.user.exception;

import com.fribuddy.common.exception.BaseException;

/**
 * 账号已锁定异常
 *
 * <h2>异常说明
 * <p>当用户账号已被锁定但仍尝试访问系统时抛出此异常。
 * <p>通常在用户登录、权限验证或需要正常用户账号的操作中触发。
 * <p>账号锁定可能由管理员操作或系统安全策略触发（如多次密码错误）。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class AccountLockedException extends BaseException {

    /**
     * 构造函数
     *
     * <h3>使用场景
     * <p>创建账号已锁定异常实例
     *
     * @param message 详细错误消息
     */
    public AccountLockedException(String message) {
        super(message);
    }
}
