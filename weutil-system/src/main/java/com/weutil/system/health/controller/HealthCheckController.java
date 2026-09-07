package com.weutil.system.health.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 *
 * <h2>功能说明
 * <p>提供轻量级存活健康检查 HTTP API。
 *
 * @module 系统运维
 * @folder 健康检查
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@RestController
public class HealthCheckController {

    // ================================ public 方法 ================================

    /**
     * 基础健康检查
     * 不依赖数据库、Redis 等外部组件，仅确认服务进程存活，适合拨测监控高频探测；组件级健康状态由 /actuator/health 提供。
     *
     * @return 固定响应字符串 "pong"
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
