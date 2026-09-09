package com.weutil.common.constants;

/**
 * 自定义 HTTP 请求头常量
 *
 * <h2>来源
 * <p>包含 API 网关层传入和项目自定义的 HTTP 请求头常量。
 *
 * <h2>命名规范
 * <p>为避免冲突，项目自定义的请求头均以 {@code x-weutil-} 开头。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public final class CustomHttpHeader {

    private CustomHttpHeader() {
        throw new UnsupportedOperationException("不允许实例化工具类");
    }

    /**
     * 请求 ID
     *
     * <h3>字段说明
     * <p>用作全链路追踪 ID
     */
    public static final String REQUEST_ID = "x-ca-request-id";

    /**
     * 用户认证令牌
     *
     * <h3>字段说明
     * <p>用于传递用户认证凭证的 token 字段值
     * <p>服务端验证该令牌以确认用户身份和访问权限
     */
    public static final String USER_TOKEN = "x-weutil-user-token";

    /**
     * 链路追踪 ID
     *
     * <h3>字段说明
     * <p>服务端生成的请求链路追踪 ID，随响应返回，便于客户端反馈问题时关联服务端日志
     */
    public static final String TRACE_ID = "x-trace-id";
}