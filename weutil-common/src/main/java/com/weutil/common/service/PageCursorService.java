package com.weutil.common.service;

import com.weutil.common.annotation.LogExecution;
import com.weutil.common.exception.PageCursorInvalidException;
import com.weutil.common.model.PageCursor;
import com.weutil.common.util.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * 分页游标服务
 *
 * <h2>服务说明
 * <p>提供分页游标的 Redis 存储和验证功能，支持基于游标的分页查询。
 * <p>通过 Redis 缓存游标信息，确保游标的有效性。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PageCursorService {

    /** 有效期 1 天 */
    private static final Duration VALIDITY_PERIOD = Duration.ofDays(1);

    /** Redis 模板服务 */
    private final RedisTemplateService redisTemplateService;

    /**
     * 构造 Redis key
     *
     * <h3>构造规则
     * <p>根据游标值构造 Redis 存储的 key，格式为：page_cursor:{cursor}
     *
     * @param cursor 游标值
     * @return Redis key
     */
    private String buildRedisKey(String cursor) {
        return "page_cursor:" + cursor;
    }

    /**
     * 载入分页游标
     *
     * <h3>处理逻辑
     * <p>从 Redis 中查找指定的游标信息，验证游标是否存在。
     * <p>只有当游标存在时，才返回 PageCursor 对象，否则抛出 PageCursorInvalidException 异常。
     *
     * @param cursor 游标值
     * @return 分页游标对象
     * @throws PageCursorInvalidException 游标不存在时抛出
     */
    @LogExecution
    public PageCursor load(String cursor) {
        // 创建 PageCursor 类型的 RedisTemplate
        RedisTemplate<String, PageCursor> redisTemplate = redisTemplateService.createRedisTemplate(PageCursor.class);

        // 从 Redis 中获取游标信息
        String redisKey = buildRedisKey(cursor);
        PageCursor pageCursor = redisTemplate.opsForValue().get(redisKey);

        // 验证游标是否存在
        if (pageCursor == null) {
            throw new PageCursorInvalidException("分页游标无效");
        }

        return pageCursor;
    }

    /**
     * 保存分页游标
     *
     * <h3>处理逻辑
     * <p>根据主字段值和主键 ID 生成分页游标，并保存到 Redis。
     * <p>使用 16 位随机字符串生成唯一游标值，设置 1 天的过期时间。
     *
     * @param primaryFieldValue 主字段值
     * @param primaryKeyId 主键 ID
     * @return 生成的游标值
     */
    @LogExecution
    public String save(Instant primaryFieldValue, Long primaryKeyId) {
        // 生成唯一游标值
        String cursor = RandomUtils.generateAlphanumeric(16);

        // 构建分页游标对象
        PageCursor pageCursor = PageCursor.builder()
            .primaryFieldValue(primaryFieldValue)
            .primaryKeyId(primaryKeyId)
            .cursor(cursor)
            .build();

        // 创建 PageCursor 类型的 RedisTemplate
        RedisTemplate<String, PageCursor> redisTemplate = redisTemplateService.createRedisTemplate(PageCursor.class);

        // 保存游标信息到 Redis，设置 1 天过期时间
        String redisKey = buildRedisKey(cursor);
        redisTemplate.opsForValue().set(redisKey, pageCursor, VALIDITY_PERIOD);

        return cursor;
    }
}
