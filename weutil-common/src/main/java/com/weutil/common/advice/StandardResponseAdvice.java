package com.weutil.common.advice;

import com.weutil.common.model.response.EmptyResponse;
import com.weutil.common.model.response.ErrorResponse;
import com.weutil.common.model.response.StandardResponse;
import com.weutil.common.service.I18nService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 标准响应转换处理类
 *
 * <h2>类说明
 * <p>自动拦截控制器返回的 ErrorResponse 与 EmptyResponse 类型响应对象，
 * 将其转换为 StandardResponse 类型，使成功与失败响应保持统一结构（errorCode + errorMessage）。
 * <p>通过 I18nService 将响应的 i18nKey 字段转换为实际的国际化消息文本。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class StandardResponseAdvice implements ResponseBodyAdvice<Object> {

    /** 国际化服务 */
    private final I18nService i18nService;

    /**
     * 判断是否需要执行 beforeBodyWrite 方法
     *
     * <h3>处理逻辑
     * <p>仅当返回类型为 ErrorResponse 或 EmptyResponse 时才进行响应体增强处理。
     *
     * @param returnType    控制器方法的返回类型
     * @param converterType 将要使用的 HTTP 消息转换器类型
     * @return 如果返回类型为 ErrorResponse 或 EmptyResponse 则返回 true，否则返回 false
     */
    @Override
    public boolean supports(MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return ErrorResponse.class.isAssignableFrom(returnType.getParameterType())
            || EmptyResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    /**
     * 在响应体写入之前对其进行处理
     *
     * <h3>处理逻辑
     * <p>将响应对象转换为 StandardResponse 对象，保持 errorCode 不变。
     * <p>错误消息按以下优先级获取：
     * <p>1. ErrorResponse 的 errorMessage 字段（业务代码显式指定的消息）
     * <p>2. 若为空，则使用 i18nKey 转换后的国际化消息
     * <p>i18nKey 无对应资源时 I18nService 抛出 NoSuchMessageException，由全局异常处理器兜底。
     *
     * @param body                  原始响应体对象，类型为 ErrorResponse 或 EmptyResponse
     * @param returnType            控制器方法的返回类型
     * @param selectedContentType   选择的内容类型
     * @param selectedConverterType 选择的消息转换器类型
     * @param request               当前 HTTP 请求
     * @param response              当前 HTTP 响应
     * @return 转换后的 StandardResponse 对象
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
        if (body instanceof ErrorResponse errorResponse) {
            String errorMessage = errorResponse.getErrorMessage() != null
                ? errorResponse.getErrorMessage()
                : i18nService.getMessage(errorResponse.getI18nKey());

            return StandardResponse
                .builder()
                .errorCode(errorResponse.getErrorCode())
                .errorMessage(errorMessage)
                .build();
        }

        EmptyResponse emptyResponse = (EmptyResponse) body;
        return StandardResponse
            .builder()
            .errorCode(emptyResponse.getErrorCode())
            .errorMessage(i18nService.getMessage(emptyResponse.getI18nKey()))
            .build();
    }
}
