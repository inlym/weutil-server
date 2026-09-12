package com.fribuddy.common.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import java.time.Duration;

/**
 * REST 客户端配置类
 *
 * <h2>配置说明
 * <p>提供统一的外部 HTTP 请求客户端配置，包括超时设置和请求日志拦截器。
 * <p>对外发起请求时，若无特殊要求，则使用当前客户端。
 * <p>日志记录由 Logbook 提供，支持结构化输出、敏感信息过滤与按条件记录。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
public class RestClientConfig {

    /**
     * 日志记录器
     *
     * <h3>配置项
     * <p>使用 Logbook 默认配置，日志输出到 Slf4j，级别由 logging.level.org.zalando.logbook 控制。
     *
     * @return Logbook 实例，不为 null
     */
    @Bean
    public Logbook logbook() {
        return Logbook.create();
    }

    /**
     * 默认 REST 客户端
     *
     * <h3>配置项
     * <p>基于 JDK 内置 HttpClient（Java 25），连接超时 5 秒，读取超时 10 秒。
     * <p>使用缓冲包装支持响应体重复读取，用于日志记录。
     * <p>通过 Logbook 拦截器记录请求与响应详情。
     *
     * @param builder Spring Boot 预配置的构建器，已注入消息转换器等默认组件
     * @param logbook Logbook 日志记录器
     * @return 配置好的 RestClient 实例，不为 null
     */
    @Bean
    public RestClient restClient(RestClient.Builder builder, Logbook logbook) {
        // 基于 JDK HttpClient 构造请求工厂，替代旧的 SimpleClientHttpRequestFactory（基于 HttpURLConnection）
        ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder
            .jdk()
            .build(
                HttpClientSettings
                    .defaults()
                    .withConnectTimeout(Duration.ofSeconds(5))
                    .withReadTimeout(Duration.ofSeconds(10))
            );

        return builder
            .requestFactory(new BufferingClientHttpRequestFactory(factory))
            .requestInterceptor(new LogbookClientHttpRequestInterceptor(logbook))
            .build();
    }
}
