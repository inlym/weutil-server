package com.fribuddy.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fribuddy.common.extension.CacheTtlCustomizer;
import com.fribuddy.common.support.jackson.GenericJackson3JsonRedisSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * Spring Cache 缓存配置类
 *
 * <h2>配置说明
 * <p>配置 Spring Cache 框架，使用 Redis 作为缓存实现，支持基于注解的声明式缓存。
 *
 * <h2>序列化策略说明
 * <p>使用自定义的 {@code GenericJackson3JsonRedisSerializer} 存储类型信息（JSON 中包含 @class 字段）。
 * <p>原因：解决反序列化时的 ClassCastException 问题（无类型信息时对象被解析为 LinkedHashMap）。
 * <p>安全评估：缓存数据仅由应用自身写入和读取，不接收外部 JSON，类型注入攻击风险可控。
 *
 * <h2>扩展机制说明
 * <p>通过 CacheTtlCustomizer 接口支持业务模块自定义缓存有效期。
 * <p>核心模块自动收集所有实现类，统一注册到 RedisCacheManager。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    /** Redis 连接工厂 */
    private final RedisConnectionFactory redisConnectionFactory;

    /** 缓存 TTL 配置定制器列表 */
    private final List<CacheTtlCustomizer> customizers;

    /** 项目统一的 JsonMapper，与 RedisTemplate 保持一致的 Instant 序列化策略（毫秒时间戳） */
    private final JsonMapper jsonMapper;

    // ================================ public 方法 ================================

    /**
     * Redis 缓存管理器 Bean
     *
     * <h3>方法说明
     * <p>配置 Redis 缓存管理器，使用 JSON 序列化存储缓存数据（含类型信息），默认缓存有效期为 10 分钟。
     *
     * <h3>缓存配置
     * <ul>
     *   <li>默认缓存：10 分钟过期</li>
     *   <li>自定义缓存：由各业务模块通过 CacheTtlCustomizer 接口配置</li>
     * </ul>
     *
     * @return 配置好的 RedisCacheManager 实例
     */
    @Bean
    public CacheManager cacheManager() {
        // 复用项目统一的 JsonMapper，保证 Spring Cache 与 RedisTemplate 对 Instant 字段的序列化方式一致（毫秒时间戳）
        // 配置默认缓存：字符串键序列化 + JSON 值序列化（含类型信息）
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
            .defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson3JsonRedisSerializer(jsonMapper)
                )
            )
            .entryTtl(Duration.ofMinutes(10))
            .computePrefixWith(cacheName -> cacheName + ":");

        // 收集所有业务模块的缓存配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        for (CacheTtlCustomizer customizer : customizers) {
            customizer.declareCacheTtl().forEach((cacheName, ttl) -> {
                // 检测重复配置并打印警告日志
                if (cacheConfigurations.containsKey(cacheName)) {
                    log.warn(
                        "缓存 {} 被 {} 重复配置，后者将覆盖前者",
                        cacheName,
                        customizer.getClass().getSimpleName()
                    );
                }
                cacheConfigurations.put(cacheName, defaultConfig.entryTtl(ttl));
            });
        }

        return RedisCacheManager
            .builder(redisConnectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .enableStatistics()
            .build();
    }
}
