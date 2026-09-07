package com.weutil.common.config;

import com.weutil.common.extension.WebSocketCustomizer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * WebSocket 配置类
 *
 * <h2>主要功能
 * <p>配置 WebSocket 端点和处理器的基础框架。
 * <p>启用 WebSocket 功能，为业务模块提供注册扩展点。
 *
 * <h2>扩展机制说明
 * <p>通过 {@code WebSocketCustomizer} 接口支持业务模块扩展配置。
 * <p>核心模块自动收集所有实现类，在初始化时依次调用。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@EnableWebSocket
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    /** WebSocket 定制器列表 */
    private final List<WebSocketCustomizer> customizers;

    // ================================ public 方法 ================================

    /**
     * 注册 WebSocket 处理器
     *
     * <h3>配置说明
     * <p>遍历所有定制器，由业务模块自行注册处理器到指定端点。
     *
     * @param registry WebSocket 处理器注册表
     */
    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        customizers.forEach(customizer -> customizer.customize(registry));
    }
}
