package com.fribuddy.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * WebSocket 配置类
 *
 * <h2>主要功能
 * <p>启用 WebSocket 功能。
 * <p>业务模块在各自 config 包下独立实现 {@code WebSocketConfigurer} 接口注册处理器，Spring 自动收集所有实现类并依次调用，无需在此聚合。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@EnableWebSocket
@Configuration
public class WebSocketConfig {
}
