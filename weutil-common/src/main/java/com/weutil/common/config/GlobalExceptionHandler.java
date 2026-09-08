package com.weutil.common.config;

import com.weutil.common.exception.ExternalApiException;
import com.weutil.common.exception.PageCursorInvalidException;
import com.weutil.common.exception.PlaceholderException;
import com.weutil.common.exception.EntityNotFoundException;
import com.weutil.common.exception.ThirdPartySdkException;
import com.weutil.common.exception.UnpredictableException;
import com.weutil.common.exception.WebSocketException;
import com.weutil.common.model.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 *
 * <h2>类说明
 * <p>统一捕获应用程序中的各种异常，并将异常信息转换为标准的错误响应。
 * <p>通过 @RestControllerAdvice 注解实现全局异常拦截，确保所有异常都能被妥善处理并返回统一格式的错误信息。
 * <p>使用较低优先级，确保模块级异常处理器优先于本处理器执行。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    /**
     * 处理分页游标无效异常
     *
     * <h3>方法说明
     * <p>当通过游标（cursor）进行分页查询时，发现游标无效、未找到或与查询条件不匹配时触发。
     *
     * @param e 分页游标无效异常
     * @return 错误响应，错误码为 6
     */
    @ExceptionHandler(PageCursorInvalidException.class)
    public ErrorResponse handlePageCursorInvalid(PageCursorInvalidException e) {
        log.trace("分页游标无效: {}", e.getMessage());
        return new ErrorResponse(6, "response.cursor.invalid");
    }

    /**
     * 处理访问拒绝异常
     *
     * <h3>方法说明
     * <p>当未携带有效登录信息访问需要登录的接口时触发，对应"未登录"场景。
     * <p>通常由 Spring Security 的 {@code @Secured} 注解检查失败时抛出，
     * 本项目 {@code @UserPermission} 注解语义为"需要登录"，故返回 401 与未登录文案。
     *
     * @param e 访问拒绝异常
     * @return 错误响应，HTTP 状态码 401，错误码为 5
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDenied(AccessDeniedException e) {
        log.warn("访问拒绝: {}", e.getMessage());
        return new ErrorResponse(5, "response.auth.not_login");
    }

    /**
     * 处理 404 异常（路径不存在）
     *
     * <h3>方法说明
     * <p>当请求的路径不存在时触发。
     *
     * @param e 路径不存在异常
     * @return 错误响应，错误码为 4
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResponse handleNoHandlerFound(NoHandlerFoundException e) {
        log.trace("请求路径不存在: {}", e.getRequestURL());
        return new ErrorResponse(4, "response.resource.invalid");
    }

    /**
     * 处理静态资源未找到异常
     *
     * <h3>方法说明
     * <p>当请求的静态资源不存在时触发。
     *
     * @param e 静态资源未找到异常
     * @return 错误响应，错误码为 4
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ErrorResponse handleNoResourceFound(NoResourceFoundException e) {
        log.trace("请求资源不存在: {} {}", e.getHttpMethod(), e.getResourcePath());
        return new ErrorResponse(4, "response.resource.invalid");
    }

    /**
     * 处理实体未找到异常
     *
     * <h3>方法说明
     * <p>当查询实体未找到或归属校验不匹配时触发。
     * <p>出于安全考虑，两种情况统一返回相同响应，避免泄露实体归属信息。
     *
     * @param e 实体未找到异常
     * @return 错误响应，错误码为 4
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException e) {
        log.trace("实体未找到: {}", e.getMessage());
        return new ErrorResponse(4, "response.resource.invalid");
    }

    /**
     * 处理非法参数异常
     *
     * <h3>方法说明
     * <p>当业务代码主动抛出 {@code IllegalArgumentException} 校验参数合法性失败时触发。
     *
     * @param e 非法参数异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e) {
        log.trace("非法参数: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理参数校验异常（@RequestBody 参数校验失败）
     *
     * <h3>方法说明
     * <p>当使用 @RequestBody 注解的参数校验失败时触发。
     *
     * @param e 方法参数校验异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.trace("请求参数校验失败: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理表单参数校验异常（表单提交参数校验失败）
     *
     * <h3>方法说明
     * <p>当表单参数校验失败时触发。
     *
     * @param e 表单绑定异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(BindException.class)
    public ErrorResponse handleBindException(BindException e) {
        log.trace("表单参数校验失败: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理约束校验异常（@Validated 单个参数校验失败）
     *
     * <h3>方法说明
     * <p>当使用 @Validated 注解进行方法参数校验失败时触发。
     *
     * @param e 约束违反异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e) {
        log.trace("约束校验失败: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理缺少请求参数异常
     *
     * <h3>方法说明
     * <p>当请求缺少必需的参数时触发。
     *
     * @param e 缺少请求参数异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.trace("缺少请求参数: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理参数类型不匹配异常
     *
     * <h3>方法说明
     * <p>当请求参数类型与方法参数类型不匹配时触发。
     *
     * @param e 参数类型不匹配异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.trace("参数类型不匹配: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理 HTTP 请求方法不支持异常
     *
     * <h3>方法说明
     * <p>当使用了不支持的 HTTP 方法时触发。
     *
     * @param e HTTP 请求方法不支持异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.trace("不支持的 HTTP 请求方法: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理内容类型不支持异常
     *
     * <h3>方法说明
     * <p>当请求的内容类型不被支持时触发。
     *
     * @param e HTTP 媒体类型不支持异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResponse handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.trace("不支持的内容类型: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理消息不可读异常（JSON 格式错误等）
     *
     * <h3>方法说明
     * <p>当请求体格式不正确或无法解析时触发。
     *
     * @param e HTTP 消息不可读异常
     * @return 错误响应，错误码为 3
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.trace("请求消息格式错误: {}", e.getMessage());
        return new ErrorResponse(3, "response.parameter.invalid");
    }

    /**
     * 处理 WebSocket 连接异常
     *
     * <h3>方法说明
     * <p>当 WebSocket 连接建立、通信过程中出现错误时触发。
     *
     * @param e WebSocket 连接异常
     * @return 错误响应，错误码为 3001
     */
    @ExceptionHandler(WebSocketException.class)
    public ErrorResponse handleWebSocketConnection(WebSocketException e) {
        log.error("WebSocket 连接异常", e);
        return new ErrorResponse(3001, "response.websocket_connection.error");
    }

    /**
     * 处理外部 API 异常
     *
     * <h3>方法说明
     * <p>当对外发起 API 请求时出现错误时触发。
     *
     * @param e 外部 API 异常
     * @return 错误响应，错误码为 3002
     */
    @ExceptionHandler(ExternalApiException.class)
    public ErrorResponse handleExternalApi(ExternalApiException e) {
        log.error("外部 API 异常", e);
        return new ErrorResponse(3002, "response.external_api.error");
    }

    /**
     * 处理第三方 SDK 异常
     *
     * <h3>方法说明
     * <p>当调用第三方 SDK 时出现错误时触发。
     *
     * @param e 第三方 SDK 异常
     * @return 错误响应，错误码为 3003
     */
    @ExceptionHandler(ThirdPartySdkException.class)
    public ErrorResponse handleThirdPartySdk(ThirdPartySdkException e) {
        log.error("第三方 SDK 异常", e);
        return new ErrorResponse(3003, "response.third_party_sdk.error");
    }

    /**
     * 处理占位异常
     *
     * <h3>方法说明
     * <p>用于代码分支结构完整性，实际不应触发此异常。
     *
     * @param e 占位异常
     * @return 错误响应，错误码为 2001
     */
    @ExceptionHandler(PlaceholderException.class)
    public ErrorResponse handlePlaceholder(PlaceholderException e) {
        log.error("占位异常被触发", e);
        return new ErrorResponse(2001, "response.placeholder.error");
    }

    /**
     * 处理意料之外异常
     *
     * <h3>方法说明
     * <p>当分支判断中出现未考虑到的情况时触发。
     *
     * @param e 意料之外异常
     * @return 错误响应，错误码为 2002
     */
    @ExceptionHandler(UnpredictableException.class)
    public ErrorResponse handleUnpredictable(UnpredictableException e) {
        log.error("意料之外异常", e);
        return new ErrorResponse(2002, "response.unpredictable.error");
    }

    /**
     * 处理其他未捕获的异常
     *
     * <h3>方法说明
     * <p>作为最后的异常处理器，捕获所有未被上述方法处理的其他异常。
     *
     * @param e 通用异常
     * @return 错误响应，错误码为 1
     */
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(Exception e) {
        log.error("未处理的异常", e);
        return new ErrorResponse(1, "response.server.error");
    }
}
