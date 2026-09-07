package com.weutil.common.model.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * 简单管理员身份验证对象
 *
 * <h2>说明
 * <p>实现 Spring Security 的 Authentication 接口，用于在 Spring Security 框架中使用。
 * <p>认证对象创建后始终处于已认证状态，不可取消认证。
 *
 * <h2>用途
 * <p>用于调试部分对系统可能有一定破坏性的接口。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class SimpleAdminAuthentication implements Authentication {

    /** 权限列表 */
    private final List<SimpleGrantedAuthority> authorities;

    /**
     * 构造简单管理员身份验证对象
     */
    public SimpleAdminAuthentication() {
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * 获取权限集合
     *
     * @return 不可修改的权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * 获取凭据
     *
     * @return 凭据（当前返回 null）
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * 获取详细信息
     *
     * @return 详细信息（当前返回 null）
     */
    @Override
    public Object getDetails() {
        return null;
    }

    /**
     * 获取主体
     *
     * @return 主体（当前返回 null）
     */
    @Override
    public Object getPrincipal() {
        return null;
    }

    /**
     * 是否已认证
     *
     * <h3>方法说明
     * <p>认证对象创建后始终处于已认证状态，此方法始终返回 true。
     *
     * @return 认证状态（始终为 true）
     */
    @Override
    public boolean isAuthenticated() {
        return true;
    }

    /**
     * 设置认证状态
     *
     * <h3>方法说明
     * <p>仅允许将认证状态设置为 true，禁止取消认证状态。
     * <p>由于认证对象创建时已处于认证状态，此方法仅用于校验不允许取消认证。
     *
     * @param authenticated 认证状态
     * @throws IllegalArgumentException 当尝试取消认证状态时抛出
     */
    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        if (!authenticated) {
            throw new IllegalArgumentException("不允许将已认证状态设置为 false");
        }
    }

    /**
     * 获取名称
     *
     * @return 名称（当前返回 null）
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * 判断两个身份验证对象是否相等
     *
     * <h3>方法说明
     * <p>由于管理员只有一个固定身份，所有 SimpleAdminAuthentication 对象视为相等。
     *
     * @param obj 比较对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleAdminAuthentication;
    }

    /**
     * 计算哈希值
     *
     * <h3>方法说明
     * <p>所有 SimpleAdminAuthentication 对象哈希值相同。
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return SimpleAdminAuthentication.class.hashCode();
    }
}