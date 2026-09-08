package com.weutil.common.annotation.resolver;

import com.weutil.common.annotation.UserId;
import com.weutil.common.constants.ContextKeys;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 用户 ID 注解方法参数解析器
 *
 * <h2>功能说明
 * <p>解析控制器方法中带有 {@code UserId} 注解的参数，从请求属性中获取用户 ID 并注入。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class UserIdMethodArgumentResolver implements HandlerMethodArgumentResolver {

    // ================================ public 方法 ================================

    /**
     * 判断参数是否支持解析
     *
     * <h3>判断条件
     * <p>参数类型为 long 基本类型且带有 {@code UserId} 注解
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return long.class == parameter.getParameterType() &&
            parameter.hasParameterAnnotation(UserId.class);
    }

    /**
     * 解析用户 ID 参数
     *
     * <h3>处理逻辑
     * <p>从请求属性中获取已认证的用户 ID，若用户 ID 不存在或无效则抛出未授权访问异常
     */
    @Override
    public Object resolveArgument(
        @NonNull MethodParameter parameter,
        ModelAndViewContainer container,
        NativeWebRequest webRequest,
        WebDataBinderFactory factory
    ) {
        Long userId = (Long) webRequest.getAttribute(ContextKeys.USER_ID, RequestAttributes.SCOPE_REQUEST);

        if (userId != null && userId > 0) {
            return userId;
        }

        throw new AccessDeniedException("访问被拒绝");
    }
}
