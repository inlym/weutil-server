package com.fribuddy.common.support.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

/**
 * 泛化版 Jackson 3.x Redis JSON 序列化器
 *
 * <h2>说明
 * <p>对应 Spring Data Redis 的 {@code GenericJackson2JsonRedisSerializer}（Jackson 2.x 专用）。
 * <p>序列化时通过 {@code @class} 字段携带原始类型信息，反序列化时按 {@code @class} 还原具体类型。
 * <p>适用于 {@code RedisTemplate<String, Object>} 等值类型不固定的场景。
 *
 * <h2>安全评估
 * <p>缓存数据仅由应用自身写入和读取，不接收外部 JSON，类型注入攻击风险可控。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@NullMarked
public class GenericJackson3JsonRedisSerializer implements RedisSerializer<Object> {

    /** 实际执行序列化的 JsonMapper，构造时已激活 DefaultTyping */
    private final JsonMapper jsonMapper;

    /**
     * 构造泛化序列化器
     *
     * <h3>处理逻辑
     * <p>基于传入 JsonMapper 重建新实例并激活 DefaultTyping，确保类型信息以 {@code @class} 属性形式存储。
     * <p>原始 JsonMapper 不被污染，可继续供其它场景使用。
     *
     * @param jsonMapper 原始 JsonMapper
     */
    public GenericJackson3JsonRedisSerializer(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper
            .rebuild()
            .activateDefaultTyping(
                BasicPolymorphicTypeValidator
                    .builder()
                    .allowIfBaseType(Object.class)
                    .build(),
                DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
            )
            .build();
    }

    @Override
    public byte[] serialize(@Nullable Object t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        // RedisSerializer 接口约定抛 SerializationException，需将 Jackson 运行时异常转换
        try {
            return jsonMapper.writeValueAsBytes(t);
        } catch (Exception e) {
            throw new SerializationException(
                String.format("序列化失败，类型：%s", t.getClass().getName()),
                e
            );
        }
    }

    @Override
    @Nullable
    public Object deserialize(byte @Nullable[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        // RedisSerializer 接口约定抛 SerializationException，需将 Jackson 运行时异常转换
        try {
            return jsonMapper.readValue(bytes, Object.class);
        } catch (Exception e) {
            throw new SerializationException("反序列化失败，目标类型：Object", e);
        }
    }
}
