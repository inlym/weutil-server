package com.weutil.common.service;

import com.weutil.common.support.jackson.Jackson3JsonRedisSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

/**
 * Redis 模板服务
 *
 * <h2>说明
 * <p>用于创建和配置 Redis 模板的工具服务，提供类型安全的 Redis 操作模板。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Service
@RequiredArgsConstructor
public class RedisTemplateService {
    /** Redis 连接工厂 */
    private final RedisConnectionFactory redisConnectionFactory;

    /** JSON 对象映射器 */
    private final JsonMapper jsonMapper;

    /**
     * 创建 Redis 模板
     *
     * <h3>用法说明
     * <p>该方法用于创建具有类型安全序列化器的 RedisTemplate 实例，支持泛型类型操作。
     * <p>创建的模板配置了 StringRedisSerializer 作为 key 的序列化器，Jackson3JsonRedisSerializer 作为 value 的序列化器。
     *
     * <p><b>使用示例：</b>
     * <pre>{@code
     * // 1. 在其他服务中注入 RedisTemplateService
     * {@literal @}RequiredArgsConstructor
     * public class UserService {
     *     private final RedisTemplateService redisTemplateService;
     *
     *     public void cacheUser(User user) {
     *         // 2. 创建针对 User 类型的 RedisTemplate
     *         RedisTemplate<String, User> userTemplate = redisTemplateService.createRedisTemplate(User.class);
     *
     *         // 3. 使用模板进行 Redis 操作
     *         userTemplate.opsForValue().set("user:" + user.getId(), user, Duration.ofHours(1));
     *     }
     *
     *     public User getUserFromCache(Long userId) {
     *         RedisTemplate<String, User> userTemplate = redisTemplateService.createRedisTemplate(User.class);
     *         return userTemplate.opsForValue().get("user:" + userId);
     *     }
     * }
     * }</pre>
     *
     * @param <T>  泛型类型参数，表示存储在 Redis 中的对象类型
     * @param type 要序列化的对象类型，用于创建 Jackson3JsonRedisSerializer
     * @return 配置好的 RedisTemplate 实例，支持指定类型的序列化和反序列化
     */
    public <T> RedisTemplate<String, T> createRedisTemplate(Class<T> type) {
        Jackson3JsonRedisSerializer<T> serializer = new Jackson3JsonRedisSerializer<>(jsonMapper, type);

        RedisTemplate<String, T> template = new RedisTemplate<>();

        // 设置连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        // 设置序列化器
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(serializer);

        // 初始化模板
        template.afterPropertiesSet();

        return template;
    }
}
