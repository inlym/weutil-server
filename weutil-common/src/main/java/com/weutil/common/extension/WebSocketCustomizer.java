package com.weutil.common.extension;

import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 定制器接口
 *
 * <h2>说明
 * <p>业务模块可实现此接口，自定义 WebSocket 处理器的注册配置。
 * <p>核心模块会自动收集所有实现类，在 WebSocket 配置初始化时依次调用。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public interface WebSocketCustomizer {

    /**
     * 定制 WebSocket 处理器注册配置
     *
     * @param registry WebSocket 处理器注册表
     */
    void customize(WebSocketHandlerRegistry registry);
}