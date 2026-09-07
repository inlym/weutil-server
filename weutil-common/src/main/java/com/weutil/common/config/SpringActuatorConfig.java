package com.weutil.common.config;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Spring Actuator 扩展配置类
 *
 * <h2>配置说明
 * <p>注册自定义的 HealthIndicator 和 InfoContributor Bean，扩展 Actuator 的监控能力。
 *
 * <h3>公网连通性检测
 * <p>通过访问外部 URL 检测公网是否可达，超时或异常时标记为 DOWN。
 *
 * <h3>时区信息贡献
 * <p>输出 JVM 默认时区、系统属性时区和 Jackson 序列化时区，输出到 /actuator/info 端点。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
public class SpringActuatorConfig {

    // ================================ 静态常量字段 ================================

    /** 公网连通性检测的目标 URL */
    private static final String HEALTH_CHECK_URL = "https://www.baidu.com";

    /** 公网连通性检测超时时间（秒） */
    private static final int HEALTH_CHECK_TIMEOUT_SECONDS = 3;

    // ================================ public 方法 ================================

    /**
     * 公网连通性健康检查指示器
     *
     * <h3>检测逻辑
     * <p>向目标公网 URL 发起 GET 请求，2xx 响应视为 UP，否则为 DOWN。
     * <p>请求超时或网络异常时返回 DOWN 并附带异常详情。
     *
     * <h3>健康详情
     * <p>UP 时包含检测 URL 和响应耗时，DOWN 时额外包含状态码或异常信息。
     *
     * @return 公网连通性 HealthIndicator 实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "internetConnectivityHealthIndicator")
    public HealthIndicator internetConnectivityHealthIndicator() {
        return () -> {
            // 创建独立的 RestClient，配置超时时间防止健康检查无限挂起
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(Duration.ofSeconds(HEALTH_CHECK_TIMEOUT_SECONDS));
            requestFactory.setReadTimeout(Duration.ofSeconds(HEALTH_CHECK_TIMEOUT_SECONDS));

            RestClient restClient = RestClient.builder().requestFactory(requestFactory).build();

            Instant startTime = Instant.now();

            HttpStatusCode statusCode;
            // RestClient 网络调用可能因超时或 DNS 解析失败抛出异常，不捕获会导致 health() 方法异常退出，/actuator/health 端点无响应
            try {
                // 向目标 URL 发起 GET 请求，检测公网连通性
                statusCode = restClient
                    .get()
                    .uri(HEALTH_CHECK_URL)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();
            } catch (Exception e) {
                // 将网络异常转换为 DOWN 状态，不向上传播，保证 /actuator/health 始终有响应
                return Health.down(e)
                    .withDetail("url", HEALTH_CHECK_URL)
                    .build();
            }

            // 计算请求耗时
            long responseTimeMs = Duration.between(startTime, Instant.now()).toMillis();

            if (statusCode.is2xxSuccessful()) {
                return Health.up()
                    .withDetail("url", HEALTH_CHECK_URL)
                    .withDetail("responseTimeMs", responseTimeMs)
                    .build();
            }

            return Health.down()
                .withDetail("url", HEALTH_CHECK_URL)
                .withDetail("statusCode", statusCode.value())
                .withDetail("responseTimeMs", responseTimeMs)
                .build();
        };
    }

    /**
     * 时区信息贡献器
     *
     * <h3>贡献信息
     * <p>输出项目内各组件时区信息，包括 JVM 默认时区、系统属性时区和 Jackson 序列化时区。
     * <p>便于排查跨时区问题，如日志时间偏移、API 时间字段解析不一致等。
     *
     * @param environment Spring 环境抽象，用于读取 Jackson 时区配置
     * @return 时区信息 InfoContributor 实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "timezoneInfoContributor")
    public InfoContributor timezoneInfoContributor(Environment environment) {
        return builder -> {
            Map<String, Object> timeZone = new LinkedHashMap<>();

            // JVM 默认时区 ID，如 Asia/Shanghai
            ZoneId jvmZoneId = ZoneId.systemDefault();
            timeZone.put("jvmDefaultZoneId", jvmZoneId.toString());

            // JVM 默认时区显示名
            TimeZone jvmTimeZone = TimeZone.getDefault();
            timeZone.put("jvmDefaultDisplayName", jvmTimeZone.getDisplayName());

            // 系统属性 user.timezone，启动参数 -Duser.timezone 传入
            String userTimezone = System.getProperty("user.timezone");
            timeZone.put("systemPropertyUserTimezone", userTimezone != null ? userTimezone : "未设置");

            // Jackson 序列化时区配置，影响 @JsonFormat 等注解的输出
            String jacksonTimezone = environment.getProperty("spring.jackson.time-zone", "未配置");
            timeZone.put("jacksonTimeZone", jacksonTimezone);

            builder.withDetail("timeZone", timeZone);
        };
    }
}
