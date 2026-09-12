package com.fribuddy.common.startup;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 应用启动时间初始化器
 *
 * <h2>功能说明
 * <p>在应用启动后自动执行，记录应用启动时间。
 * <p>提供 Getter 方法供其他模块通过依赖注入获取启动时间。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Component
public class StartupTimeInitializer implements ApplicationRunner {

    /** 应用启动时间 */
    @Getter
    private Instant startupTime;

    /**
     * 应用启动后执行启动时间记录
     *
     * <h3>执行时机
     * <p>在 Spring Boot 应用启动完成后自动执行，优先级高于普通的 Bean 初始化。
     *
     * <h3>处理逻辑
     * <p>记录当前时间作为应用启动时间，用于后续计算运行时长。
     */
    @Override
    public void run(@NonNull ApplicationArguments args) {
        // 记录应用启动时间
        startupTime = Instant.now();

        // 记录启动日志
        log.info("应用启动完成，启动时间: {}", startupTime);
    }
}
