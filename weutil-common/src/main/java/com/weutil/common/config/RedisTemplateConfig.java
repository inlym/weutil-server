package com.weutil.common.config;

import com.weutil.common.support.jackson.GenericJackson3JsonRedisSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * Redis 模板配置类
 *
 * <h2>配置说明
 * <p>定义两个 RedisTemplate Bean：对象存储用 JSON 序列化，二进制数据用字节数组原生序列化。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
@RequiredArgsConstructor
public class RedisTemplateConfig {

    /** Redis 连接工厂 */
    private final RedisConnectionFactory redisConnectionFactory;

    /** Redis 值 JSON 序列化所用的对象映射器 */
    private final JsonMapper jsonMapper;

    /**
     * 对象 Redis 模板 Bean
     *
     * <h3>序列化策略
     * <p>值采用 JSON 序列化并通过 @class 字段保留类型信息，反序列化时自动还原为原始对象。
     *
     * @return 配置好的 RedisTemplate 实例，键使用字符串序列化，值使用 JSON 序列化
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 复用容器内的连接工厂，避免每个模板各自建连
        template.setConnectionFactory(redisConnectionFactory);

        // key 与 hash key 用字符串序列化，便于 redis-cli 直接查看
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 值与 hash 值用 JSON 序列化，通过 @class 字段保留类型信息，反序列化时还原为原始对象
        template.setValueSerializer(new GenericJackson3JsonRedisSerializer(jsonMapper));
        template.setHashValueSerializer(new GenericJackson3JsonRedisSerializer(jsonMapper));

        // 触发初始化钩子，使上面的序列化器配置生效
        template.afterPropertiesSet();

        return template;
    }

    /**
     * 字节数组 Redis 模板 Bean
     *
     * <h3>适用场景
     * <p>用于音频、图片等二进制内容的存取，避免 JSON 编解码带来的开销与潜在损坏。
     *
     * @return 配置好的 RedisTemplate 实例，键使用字符串序列化，值使用字节数组序列化
     */
    @Bean
    public RedisTemplate<String, byte[]> redisTemplateBytes() {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();

        // 复用容器内的连接工厂，避免每个模板各自建连
        template.setConnectionFactory(redisConnectionFactory);

        // key 与 hash key 用字符串序列化，便于 redis-cli 直接查看
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 值与 hash 值用字节数组原生序列化，保留二进制内容不做编码转换
        template.setValueSerializer(RedisSerializer.byteArray());
        template.setHashValueSerializer(RedisSerializer.byteArray());

        // 触发初始化钩子，使上面的序列化器配置生效
        template.afterPropertiesSet();

        return template;
    }
}
