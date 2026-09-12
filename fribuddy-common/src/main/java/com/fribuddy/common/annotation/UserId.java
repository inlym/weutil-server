package com.fribuddy.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户 ID 注入器
 *
 * <h2>主要用途
 * <p>在控制器方法的参数中注入用户 ID，以便在方法内部快捷获取和使用。
 *
 * <h2>注意事项
 * <p>需要结合 {@code @UserPermission} 注解使用（登录鉴权通过才会有用户 ID）。
 * <p>在控制器方法参数注入 {@code @UserId long userId}。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserId {
}