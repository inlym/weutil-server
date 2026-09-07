package com.weutil.account.user.exception;

import com.weutil.common.exception.BaseException;

/**
 * 用户未找到异常
 *
 * <h2>异常说明
 * <p>当通过用户 ID 查询用户时未找到对应的记录时抛出此异常。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class UserNotFoundException extends BaseException {

    /**
     * 构造函数
     *
     * <h3>使用场景
     * <p>创建用户未找到异常实例
     *
     * @param message 详细错误消息
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
