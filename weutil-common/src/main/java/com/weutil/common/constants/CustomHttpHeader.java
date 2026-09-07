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
public abstract class CustomHttpHeader {

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
     * 管理员令牌
     *
     * <h3>字段说明
     * <p>用于传递管理员认证凭证的 token 字段值
     * <p>服务端验证该令牌以确认管理员身份和访问权限
     */
    public static final String ADMIN_TOKEN = "x-weutil-admin-token";

    /**
     * 设备令牌
     *
     * <h3>字段说明
     * <p>用于传递设备认证凭证的 token 字段值
     * <p>服务端验证该令牌以确认设备身份和访问权限
     */
    public static final String DEVICE_TOKEN = "x-weutil-device-token";

    /** 客户端 IP 地址 */
    public static final String CLIENT_IP = "x-forwarded-for";

    /**
     * 转发信息
     *
     * <h3>字段说明
     * <p>由代理服务器添加的转发信息，包含客户端 IP、代理信息等
     * <p>格式示例：`by=3.235.39.85;for=220.188.248.81;host=api.weutil.com;proto=https`
     */
    public static final String FORWARDED = "forwarded";

    /**
     * 客户端信息
     *
     * <h3>字段说明
     * <p>组合多项键值对，使用 `; ` 分割
     * <p>文本格式示例：`key1=value1; key2=value2; key3=value3`
     */
    public static final String CLIENT_INFO = "x-weutil-client-info";
}