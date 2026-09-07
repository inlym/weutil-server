package com.weutil.common.config;

import com.weutil.common.support.http.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * REST 客户端配置类
 *
 * <h2>配置说明
 * <p>提供统一的外部 HTTP 请求客户端配置，包括超时设置和请求日志拦截器。
 * <p>对外发起请求时，若无特殊要求，则使用当前客户端。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
public class RestClientConfig {

    /**
     * 默认 REST 客户端
     *
     * <h3>配置项
     * <p>连接超时 5 秒，读取超时 10 秒。
     * <p>使用缓冲包装支持响应体重复读取，用于日志记录。
     *
     * @return 配置好的 RestClient 实例
     */
    @Bean
    public RestClient restClient() {
        // 配置请求超时参数
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);

        return RestClient
            .builder()
            .requestFactory(new BufferingClientHttpRequestFactory(requestFactory))
            .requestInterceptor(new HttpLoggingInterceptor())
            .build();
    }
}