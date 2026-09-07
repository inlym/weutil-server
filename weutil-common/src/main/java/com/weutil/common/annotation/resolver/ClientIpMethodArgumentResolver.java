package com.weutil.common.annotation.resolver;

import com.weutil.common.annotation.ClientIp;
import com.weutil.common.constants.ContextKeys;
import com.weutil.common.exception.UnpredictableException;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 客户端 IP 地址注解方法参数解析器
 *
 * <h2>功能说明
 * <p>解析控制器方法中带有 {@code ClientIp} 注解的参数，从请求属性中获取客户端 IP 地址并注入。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class ClientIpMethodArgumentResolver implements HandlerMethodArgumentResolver {

    // ================================ public 方法 ================================

    /**
     * 判断参数是否支持解析
     *
     * <h3>判断条件
     * <p>参数类型为 String 且带有 {@code ClientIp} 注解
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(String.class) &&
            parameter.hasParameterAnnotation(ClientIp.class);
    }

    /**
     * 解析客户端 IP 地址参数
     *
     * <h3>处理逻辑
     * <p>从请求属性中获取客户端 IP 地址，若未获取到则抛出不可预测异常
     */
    @Override
    public Object resolveArgument(
        @NonNull MethodParameter parameter,
        ModelAndViewContainer container,
        NativeWebRequest webRequest,
        WebDataBinderFactory factory
    ) {
        String clientIp = (String) webRequest.getAttribute(ContextKeys.CLIENT_IP, RequestAttributes.SCOPE_REQUEST);

        if (clientIp != null) {
            return clientIp;
        }

        throw new UnpredictableException("未获取到客户端 IP 地址");
    }
}
