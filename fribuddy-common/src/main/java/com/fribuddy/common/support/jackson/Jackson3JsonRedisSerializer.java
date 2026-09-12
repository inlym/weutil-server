package com.fribuddy.common.support.jackson;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson 3.x 实现的 Redis JSON 序列化器
 *
 * <h2>说明
 * <p>Spring Data Redis 4.x 仅提供 {@code Jackson2JsonRedisSerializer}（Jackson 2.x 专用），
 * <p>无 Jackson 3.x 对应实现。本类基于 Jackson 3.x {@code JsonMapper} 重新实现等价功能。
 *
 * <h2>具化模式（类型已知）
 * <p>构造时传入 {@code javaType}，按指定类型直接序列化与反序列化，不附加类型信息。
 * <p>适用于 {@code RedisTemplate<String, T>} 场景，避免冗余的 {@code @class} 字段。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@NullMarked
public class Jackson3JsonRedisSerializer<T> implements RedisSerializer<T> {

    /** 实际执行序列化的 JsonMapper */
    private final JsonMapper jsonMapper;

    /** 反序列化目标类型 */
    private final Class<T> javaType;

    /**
     * 构造具化序列化器
     *
     * @param jsonMapper 原始 JsonMapper，不会被修改
     * @param javaType   反序列化目标类型
     */
    public Jackson3JsonRedisSerializer(JsonMapper jsonMapper, Class<T> javaType) {
        this.jsonMapper = jsonMapper;
        this.javaType = javaType;
    }

    @Override
    public byte[] serialize(@Nullable T t) throws SerializationException {
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
    public T deserialize(byte @Nullable[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        // RedisSerializer 接口约定抛 SerializationException，需将 Jackson 运行时异常转换
        try {
            return jsonMapper.readValue(bytes, javaType);
        } catch (Exception e) {
            throw new SerializationException(
                String.format("反序列化失败，目标类型：%s", javaType.getName()),
                e
            );
        }
    }
}
