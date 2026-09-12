package com.fribuddy.common.config;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Spring Actuator 扩展配置类
 *
 * <h2>配置说明
 * <p>注册自定义的诊断端点和 InfoContributor Bean，扩展 Actuator 的监控能力。
 *
 * <h3>公网连通性检测
 * <p>通过访问外部 URL 检测公网是否可达，以独立端点（/actuator/internet）暴露，检测结果缓存 60 秒。
 * <p>不注册为 HealthIndicator：公网连通性属于观测信息而非存活条件，避免外网波动拖累 /actuator/health 整体状态引发误杀。
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
    private static final String INTERNET_CHECK_URL = "https://www.baidu.com";

    /** 公网连通性检测超时时间（秒） */
    private static final int INTERNET_CHECK_TIMEOUT_SECONDS = 3;

    // ================================ public 方法 ================================

    /**
     * 公网连通性诊断端点
     *
     * <h3>端点信息
     * <p>对外路径为 /actuator/internet，返回公网连通性检测结果。
     *
     * @return 公网连通性诊断端点实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "internetConnectivityEndpoint")
    public InternetConnectivityEndpoint internetConnectivityEndpoint() {
        return new InternetConnectivityEndpoint();
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

    /**
     * 公网连通性诊断端点
     *
     * <h2>端点说明
     * <p>以独立 Actuator 端点暴露公网连通性检测结果，不参与 /actuator/health 的状态聚合，
     * <p>外网不可达仅体现在本端点的返回值中，不会导致健康检查整体 DOWN。
     *
     * @author <a href="https://www.inlym.com">inlym</a>
     * @since 2026-09-09
     */
    @Endpoint(id = "internet")
    public static class InternetConnectivityEndpoint {

        /** 检测结果缓存有效期（毫秒） */
        private static final long CACHE_TTL_MILLIS = 60_000;

        /** 执行检测请求的 REST 客户端，构造时创建一次 */
        private final RestClient restClient;

        /** 缓存的检测结果 */
        private volatile Map<String, Object> cachedResult;

        /** 缓存结果的生成时间戳（epoch 毫秒） */
        private volatile long cachedAtMillis;

        /**
         * 构造公网连通性诊断端点
         *
         * <h3>客户端配置
         * <p>创建独立的 RestClient 并配置超时时间，防止检测请求无限挂起；
         * <p>不复用容器内的 RestClient，避免检测流量经过 Logbook 日志拦截器。
         */
        public InternetConnectivityEndpoint() {
            ClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder
                .jdk()
                .build(
                    HttpClientSettings
                        .defaults()
                        .withConnectTimeout(Duration.ofSeconds(INTERNET_CHECK_TIMEOUT_SECONDS))
                        .withReadTimeout(Duration.ofSeconds(INTERNET_CHECK_TIMEOUT_SECONDS))
                );

            this.restClient = RestClient.builder().requestFactory(requestFactory).build();
        }

        /**
         * 查询公网连通性
         *
         * <h3>缓存策略
         * <p>检测结果缓存 60 秒，缓存有效期内直接返回缓存值，避免高频拨测放大外网请求。
         * <p>缓存过期瞬间的并发请求可能重复发起检测，结果幂等，无需加锁。
         *
         * @return 检测结果，包含状态（UP/DOWN）、检测 URL、响应耗时，失败时附状态码或异常信息
         */
        @ReadOperation
        public Map<String, Object> connectivity() {
            Map<String, Object> result = cachedResult;

            if (result != null && System.currentTimeMillis() - cachedAtMillis < CACHE_TTL_MILLIS) {
                return result;
            }

            result = doCheck();
            cachedResult = result;
            cachedAtMillis = System.currentTimeMillis();

            return result;
        }

        /**
         * 执行公网连通性检测
         *
         * <h3>检测逻辑
         * <p>向目标公网 URL 发起 GET 请求，2xx 响应视为 UP，否则为 DOWN。
         * <p>请求超时或网络异常时返回 DOWN 并附带异常信息，端点始终有响应。
         *
         * @return 检测结果
         */
        private Map<String, Object> doCheck() {
            Instant startTime = Instant.now();

            // RestClient 网络调用可能因超时或 DNS 解析失败抛出异常，
            // 捕获后转换为 DOWN 结果返回，保证端点始终有响应
            try {
                HttpStatusCode statusCode = restClient
                    .get()
                    .uri(INTERNET_CHECK_URL)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();

                // 计算请求耗时
                long responseTimeMs = Duration.between(startTime, Instant.now()).toMillis();

                Map<String, Object> result = new LinkedHashMap<>();
                result.put("status", statusCode.is2xxSuccessful() ? "UP" : "DOWN");
                result.put("url", INTERNET_CHECK_URL);
                result.put("responseTimeMs", responseTimeMs);

                // 非 2xx 响应时附带状态码，便于定位失败原因
                if (!statusCode.is2xxSuccessful()) {
                    result.put("statusCode", statusCode.value());
                }

                return result;
            } catch (Exception e) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("status", "DOWN");
                result.put("url", INTERNET_CHECK_URL);
                result.put("error", String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));

                return result;
            }
        }
    }
}
