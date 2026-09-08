package com.weutil.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 配置类
 *
 * <h2>主要功能
 * <p>配置无状态安全策略，已启用方法级鉴权（{@code @Secured} 注解）。
 *
 * <h2>安全策略说明
 * <p>HTTP 层所有请求路径允许匿名访问，权限控制由方法级 {@code @Secured} 注解配合 SecurityContext 实现。
 * <p>禁用表单登录、HTTP Basic、CSRF 和会话管理，适配无状态 API 架构。
 *
 * <h2>SecurityContext 管理
 * <p>使用 {@code requireExplicitSave(false)} 配置，允许 Filter 直接设置的 SecurityContext 在请求生命周期内保持有效。
 *
 * <h2>CORS 配置
 * <p>通过 Spring Security CorsFilter 统一管理跨域请求，在认证过滤器之前执行，确保预检请求不被拦截。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig {

    // ================================ public 方法 ================================

    /**
     * 配置安全过滤器链
     *
     * <h3>安全注意事项
     * <p>当前配置为完全开放策略，生产环境部署前应评估安全风险并配置相应的认证和授权机制。
     *
     * <h3>配置说明
     * <p>禁用表单登录、HTTP Basic、CSRF、匿名认证和会话管理，适配无状态 API 架构。
     * <p>显式关闭 frameOptions 和 HSTS 响应头，避免对 API 响应产生无意义限制。
     *
     * @param http                  HttpSecurity 配置对象
     * @param corsConfigurationSource CORS 配置源 Bean
     * @return 配置好的安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        http
            // 关闭 form 表单认证，禁用基于表单的用户名密码登录方式
            .formLogin(AbstractHttpConfigurer::disable)
            // 关闭 HTTP Basic 认证
            .httpBasic(AbstractHttpConfigurer::disable)
            // 关闭 CSRF 防护，适用于无状态的 API 接口
            .csrf(AbstractHttpConfigurer::disable)
            // 关闭匿名认证，无状态 API 不需要匿名用户概念
            .anonymous(AbstractHttpConfigurer::disable)
            // 使用 NullRequestCache，避免无状态 API 产生不必要的会话依赖
            .requestCache(cache -> cache.requestCache(new NullRequestCache()))
            // 配置无状态会话管理，不创建 HTTP 会话
            .sessionManagement(registry -> registry.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置 SecurityContext，允许 Filter 直接设置的上下文保持有效
            .securityContext(securityContext -> securityContext.requireExplicitSave(false))
            // 显式配置安全响应头，关闭对 API 无意义的 frameOptions 和 HSTS
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable)
            )
            // 配置 CORS，使用统一的 CorsConfigurationSource Bean
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // 配置所有请求路径允许匿名访问
            .authorizeHttpRequests(registry -> registry.anyRequest().permitAll());

        return http.build();
    }

    /**
     * 创建 CORS 配置源
     *
     * <h3>配置说明
     * <p>允许所有来源跨域访问，支持路径规范允许的全部 HTTP 方法，预检请求缓存 10 天。
     *
     * @return CORS 配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedHeader("*");
        configuration.setMaxAge(864000L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 创建空用户详情服务
     *
     * <h3>配置说明
     * <p>提供空的 InMemoryUserDetailsManager 实例，阻止 Spring Boot 自动配置生成默认密码并输出警告日志。
     * <p>由于本模块关闭了表单登录和 HTTP Basic 认证，此 Bean 仅用于抑制自动配置，不参与实际认证流程。
     *
     * @return 空用户详情服务
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }
}
