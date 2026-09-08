package com.weutil.common.config;

import com.weutil.common.util.JsonUtils;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson JSON 序列化配置类
 *
 * <h2>配置说明
 * <p>通过 JsonMapperBuilderCustomizer 向 Spring Boot 自动配置的 JsonMapper 注入项目统一序列化策略。
 * <p>具体策略集中定义在 JsonUtils 中，与静态场景使用的 JsonMapper 保持同一份配置。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
public class JacksonConfig {

    /**
     * 定制 Spring Boot 自动配置的 JsonMapper
     *
     * @return JsonMapper 构建器定制器，不为 null
     */
    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return JsonUtils::customizeBuilder;
    }
}
