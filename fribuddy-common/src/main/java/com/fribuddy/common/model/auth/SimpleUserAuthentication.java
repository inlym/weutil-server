package com.fribuddy.common.model.auth;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 简单用户身份验证对象
 *
 * <h2>说明
 * <p>实现 Spring Security 的 Authentication 接口，用于在 Spring Security 框架中使用。
 * <p>认证对象创建后始终处于已认证状态，不可取消认证。
 *
 * <h2>用途
 * <p>通过身份验证后，将用户相关信息生成身份验证凭证。
 *
 * <h2>注意事项
 * <p>当前只用到用户 ID 和用户角色信息。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class SimpleUserAuthentication implements Authentication {

    /** 用户 ID */
    @Getter
    private final Long userId;

    /** 权限列表 */
    private final List<SimpleGrantedAuthority> authorities;

    /**
     * 构造简单用户身份验证对象
     *
     * @param userId 用户 ID
     * @throws IllegalArgumentException 当用户 ID 为空时抛出
     */
    public SimpleUserAuthentication(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        this.userId = userId;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
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
     * @return 用户 ID 作为主体
     */
    @Override
    public Object getPrincipal() {
        return this.userId;
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
     * <p>基于用户 ID 进行相等性判断，忽略权限列表字段。
     * <p>同一用户的身份验证对象视为相等，无论权限配置如何变化。
     *
     * @param obj 比较对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SimpleUserAuthentication that = (SimpleUserAuthentication) obj;
        return Objects.equals(userId, that.userId);
    }

    /**
     * 计算哈希值
     *
     * <h3>方法说明
     * <p>基于用户 ID 计算哈希值
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}