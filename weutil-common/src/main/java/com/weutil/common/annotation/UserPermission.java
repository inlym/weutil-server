package com.weutil.common.annotation;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户登录权限鉴权注解
 *
 * <h2>主要用途
 * <p>拦截需要用户登录的控制器方法，未携带有效登录信息则直接报错。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Secured({"ROLE_USER"})
public @interface UserPermission {
}