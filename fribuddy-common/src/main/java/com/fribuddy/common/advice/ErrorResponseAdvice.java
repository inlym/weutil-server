package com.fribuddy.common.advice;

import com.fribuddy.common.model.response.ErrorInfo;
import com.fribuddy.common.model.response.ErrorResponse;
import com.fribuddy.common.service.I18nService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 错误响应转换处理类
 *
 * <h2>类说明
 * <p>自动拦截 ErrorInfo 类型的响应对象，将其转换为 ErrorResponse 类型，
 * 使错误响应保持统一结构（error.code + error.message）。
 * <p>通过 I18nService 将响应的 i18nKey 字段转换为实际的国际化消息文本。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-09
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ErrorResponseAdvice implements ResponseBodyAdvice<Object> {

    /** 兜底消息键，i18nKey 无对应资源时使用 */
    private static final String FALLBACK_MESSAGE_KEY = "response.server.error";

    /** 国际化服务 */
    private final I18nService i18nService;

    // ================================ public 方法 ================================

    /**
     * 判断是否需要执行 beforeBodyWrite 方法
     *
     * <h3>处理逻辑
     * <p>仅当返回类型为 ErrorInfo 时才进行响应体增强处理。
     *
     * @param returnType    控制器方法的返回类型
     * @param converterType 将要使用的 HTTP 消息转换器类型
     * @return 如果返回类型为 ErrorInfo 则返回 true，否则返回 false
     */
    @Override
    public boolean supports(MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return ErrorInfo.class.isAssignableFrom(returnType.getParameterType());
    }

    /**
     * 在响应体写入之前对其进行处理
     *
     * <h3>处理逻辑
     * <p>将响应对象转换为 ErrorResponse 对象，保持 code 不变。
     * <p>错误消息由 i18nKey 经国际化解析得到，无对应资源时回退到服务器错误消息。
     *
     * @param body                  原始响应体对象，类型为 ErrorInfo
     * @param returnType            控制器方法的返回类型
     * @param selectedContentType   选择的内容类型
     * @param selectedConverterType 选择的消息转换器类型
     * @param request               当前 HTTP 请求
     * @param response              当前 HTTP 响应
     * @return 转换后的 ErrorResponse 对象
     */
    @Override
    public Object beforeBodyWrite(
        Object body,
        @NonNull MethodParameter returnType,
        @NonNull MediaType selectedContentType,
        @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
        @NonNull ServerHttpRequest request,
        @NonNull ServerHttpResponse response
    ) {
        ErrorInfo errorInfo = (ErrorInfo) body;

        return new ErrorResponse(errorInfo.getCode(), resolveMessage(errorInfo.getI18nKey()));
    }

    // ================================ private 方法 ================================

    /**
     * 解析国际化消息
     *
     * <h3>兜底逻辑
     * <p>消息键无对应资源时记录 WARN 日志，并回退到服务器错误消息，保证错误响应正常输出。
     *
     * @param key 国际化消息键
     * @return 本地化消息内容
     */
    private String resolveMessage(String key) {
        // 响应体写入阶段抛出的异常不会进入全局异常处理器，缺失的消息键必须本地兜底
        try {
            return i18nService.getMessage(key);
        } catch (NoSuchMessageException e) {
            log.warn("国际化消息键不存在，回退到服务器错误消息，key={}", key);
            return i18nService.getMessage(FALLBACK_MESSAGE_KEY);
        }
    }
}
