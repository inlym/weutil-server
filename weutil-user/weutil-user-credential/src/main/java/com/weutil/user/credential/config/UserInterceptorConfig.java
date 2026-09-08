package com.weutil.user.credential.config;

import com.weutil.user.credential.interceptor.UserTokenInterceptor;
import com.weutil.user.credential.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户拦截器配置
 *
 * <h2>说明
 * <p>向 Spring MVC 注册用户令牌拦截器。
 * <p>拦截器对所有路径生效，由拦截器内部通过 @UserPermission 注解判断是否需要处理。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
@RequiredArgsConstructor
public class UserInterceptorConfig implements WebMvcConfigurer {

    /** 用户认证凭证服务 */
    private final UserCredentialService userCredentialService;

    // ================================ public 方法 ================================

    /**
     * 注册拦截器
     *
     * <h3>配置说明
     * <p>将 UserTokenInterceptor 注册到 Spring MVC 拦截器链，对所有路径生效。
     * <p>拦截器内部通过 @UserPermission 注解判断是否需要处理。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserTokenInterceptor(userCredentialService)).addPathPatterns("/**");
    }
}
