package com.fribuddy.user.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 *
 * <h2>说明
 * <p>定义用户的各种状态，用于用户管理和访问控制。
 * <p>配合 MyBatis-Flex 框架实现数据库字段的自动映射。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    // ================================ 枚举常量 ================================

    /**
     * 正常状态
     *
     * <h3>字段说明
     * <p>用户处于正常可用状态，可以正常登录和使用系统功能
     */
    NORMAL(0),

    /**
     * 已锁定状态
     *
     * <h3>字段说明
     * <p>用户被锁定，无法登录或使用系统功能
     * <p>通常由管理员操作或系统安全策略触发
     */
    LOCKED(1),

    /**
     * 已注销状态
     *
     * <h3>字段说明
     * <p>用户账号已注销，无法登录或使用系统功能
     * <p>由用户操作主动发起账号注销
     */
    CANCELLED(2);

    // ================================ 枚举字段 ================================

    /**
     * 状态码
     *
     * <h3>字段说明
     * <p>用于数据库存储
     */
    @EnumValue
    private final Integer code;
}
