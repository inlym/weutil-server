package com.weutil.common.startup;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

/**
 * 时区初始化器
 *
 * <h2>功能说明
 * <p>在应用启动后自动执行，将 JVM 默认时区设置为 UTC。
 * <p>确保应用中所有时间相关操作都使用统一的 UTC 时区，避免时区差异导致的数据不一致问题。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Component
public class TimeZoneInitializer implements ApplicationRunner {

    /** UTC 时区 ID */
    private static final String UTC_TIME_ZONE_ID = "UTC";

    /**
     * 应用启动后执行时区初始化
     *
     * <h3>执行时机
     * <p>在 Spring Boot 应用启动完成后自动执行，优先级高于普通的 Bean 初始化。
     *
     * <h3>处理逻辑
     * <p>将 JVM 默认时区设置为 UTC，确保整个应用的时间处理统一使用 UTC 时区。
     */
    @Override
    public void run(@NonNull ApplicationArguments args) {
        // 设置 JVM 默认时区为 UTC
        TimeZone.setDefault(TimeZone.getTimeZone(UTC_TIME_ZONE_ID));

        // 记录当前默认时区
        log.info("应用默认时区已设置为: {}", TimeZone.getDefault().getID());
    }
}
