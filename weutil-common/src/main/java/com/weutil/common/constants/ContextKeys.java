package com.weutil.common.constants;

/**
 * 上下文键名常量定义类
 *
 * <h2>说明
 * <p>定义应用中上下文相关的键名常量，用于在请求上下文中存储和获取元数据。
 * <p>统一管理上下文键名，避免硬编码散落在各个业务代码中。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public final class ContextKeys {

    private ContextKeys() {
        throw new UnsupportedOperationException("不允许实例化工具类");
    }

    // ================================ 上下文键名 ================================

    /** 链路追踪 ID */
    public static final String TRACE_ID = "TRACE_ID";

    /** 客户端 IP 地址 */
    public static final String CLIENT_IP = "CLIENT_IP";

    /** 用户 ID */
    public static final String USER_ID = "USER_ID";

    /** 设备 ID */
    public static final String DEVICE_ID = "DEVICE_ID";

    /** 设备编码 */
    public static final String DEVICE_CODE = "DEVICE_CODE";

    /** 聊天对话 ID */
    public static final String CHAT_CONVERSATION_ID = "CHAT_CONVERSATION_ID";

    /** 认证令牌 */
    public static final String TOKEN = "TOKEN";
}