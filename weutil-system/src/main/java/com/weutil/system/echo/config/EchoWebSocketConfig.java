package com.weutil.system.echo.config;

import com.weutil.system.echo.handler.EchoWebSocketHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 回显 WebSocket 配置
 *
 * <h2>类说明
 * <p>注册回显 WebSocket 处理器及其端点路径，客户端连接后发送文本消息可收到相同内容。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-09
 */
@Configuration
@RequiredArgsConstructor
public class EchoWebSocketConfig implements WebSocketConfigurer {

    /** 回显 WebSocket 处理器 */
    private final EchoWebSocketHandler echoWebSocketHandler;

    // ================================ public 方法 ================================

    /**
     * 注册 WebSocket 处理器
     *
     * @param registry WebSocket 处理器注册表
     */
    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(echoWebSocketHandler, "/ws/echo");
    }
}
