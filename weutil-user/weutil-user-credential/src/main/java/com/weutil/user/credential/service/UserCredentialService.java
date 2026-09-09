package com.weutil.user.credential.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.weutil.user.credential.config.UserCredentialCacheTtlCustomizer;
import com.weutil.user.credential.entity.UserCredential;
import com.weutil.user.credential.mapper.UserCredentialMapper;
import com.weutil.common.annotation.LogExecution;
import com.weutil.common.util.RandomUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.weutil.user.credential.entity.table.UserCredentialTableDef.USER_CREDENTIAL;

/**
 * 用户认证凭证服务类
 *
 * <h2>业务说明
 * <p>提供用户认证凭证的创建、查询和续期功能，用于用户身份认证和会话管理。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserCredentialService {

    /** 认证令牌长度 */
    private static final int TOKEN_LENGTH = 32;

    /** 默认有效期 */
    private static final Duration DEFAULT_VALIDITY_PERIOD = Duration.ofDays(30);

    /** 用户认证凭证数据访问层 */
    private final UserCredentialMapper userCredentialMapper;

    /** 缓存管理器，用于按令牌精确清除缓存条目 */
    private final CacheManager cacheManager;

    // ================================ public 方法 ================================

    /**
     * 创建用户认证凭证
     *
     * <h3>处理逻辑
     * <p>生成指定位数的随机字母数字字符串作为认证令牌。
     * <p>计算凭证过期时间为当前时间加上默认有效期。
     * <p>构建用户认证凭证实体对象并存入数据库。
     *
     * @param userId 用户 ID
     * @return 创建后的用户认证凭证，不为 null
     */
    @LogExecution
    public UserCredential create(Long userId) {
        // 生成随机令牌并计算过期时间
        String token = RandomUtils.generateAlphanumeric(TOKEN_LENGTH);
        Instant expireTime = Instant.now().plus(DEFAULT_VALIDITY_PERIOD);

        // 构建凭证实体并持久化
        UserCredential credential = UserCredential
            .builder()
            .userId(userId)
            .token(token)
            .expireTime(expireTime)
            .renewalCount(0)
            .build();

        userCredentialMapper.insertSelective(credential);

        log.info("创建用户认证凭证，ID：{}，用户 ID：{}", credential.getId(), userId);

        return credential;
    }

    /**
     * 续期用户认证凭证
     *
     * <h3>处理逻辑
     * <p>重新计算过期时间为当前时间加上默认有效期。
     * <p>同时更新续期次数和上次续期时间字段。
     * <p>续期次数由数据库原子自增，入参凭证可能来自缓存，内存读改写会导致计数丢失或回退。
     *
     * <h3>缓存策略
     * <p>使用 @CacheEvict 注解，续期时清除缓存，下次查询时重新加载最新数据。
     *
     * @param credential 用户认证凭证实体对象
     */
    @CacheEvict(value = UserCredentialCacheTtlCustomizer.CACHE_USER_CREDENTIAL_TOKEN, key = "#credential.token")
    @LogExecution
    public void renew(UserCredential credential) {
        Instant now = Instant.now();

        // renewal_count 需要数据库层面原子自增，Builder 方式无法表达此语义
        UpdateChain.of(UserCredential.class)
            .set(USER_CREDENTIAL.EXPIRE_TIME, now.plus(DEFAULT_VALIDITY_PERIOD))
            .setRaw(USER_CREDENTIAL.RENEWAL_COUNT, "renewal_count + 1")
            .set(USER_CREDENTIAL.LAST_RENEWAL_TIME, now)
            .where(USER_CREDENTIAL.ID.eq(credential.getId()))
            .update();

        log.info("续期用户认证凭证，ID：{}，用户 ID：{}", credential.getId(), credential.getUserId());
    }

    /**
     * 吊销用户全部认证凭证
     *
     * <h3>处理逻辑
     * <p>将该用户所有凭证的过期时间批量置为过去时刻，使其全部立即失效。
     * <p>用于账户注销、封禁等需要立即终止用户会话的场景。
     *
     * <h3>缓存策略
     * <p>缓存键为令牌，与吊销的用户维度不一致，因此先查询该用户的全部令牌，
     * <p>更新数据库后按令牌逐个精确清除缓存，避免全量清空影响其他用户。
     * <p>缓存不可用时降级为不清除，残留条目随缓存 TTL 过期失效。
     *
     * @param userId 用户 ID
     */
    @LogExecution
    public void revokeByUserId(Long userId) {
        // 查询该用户全部凭证的令牌，用于吊销后精确清除缓存
        List<String> tokens = userCredentialMapper.selectObjectListByQueryAs(
            QueryWrapper.create().select(USER_CREDENTIAL.TOKEN).where(USER_CREDENTIAL.USER_ID.eq(userId)),
            String.class
        );

        // 批量按条件置过期，Builder 方式仅支持按主键更新，无法表达此语义
        UpdateChain.of(UserCredential.class)
            .set(USER_CREDENTIAL.EXPIRE_TIME, Instant.now().minusSeconds(1))
            .where(USER_CREDENTIAL.USER_ID.eq(userId))
            .update();

        log.info("吊销用户全部认证凭证，用户 ID：{}，共 {} 条凭证", userId, tokens.size());

        // 缓存键为令牌而非用户 ID，需逐个精确清除，不能全量清空
        Cache cache = cacheManager.getCache(UserCredentialCacheTtlCustomizer.CACHE_USER_CREDENTIAL_TOKEN);
        if (cache == null) {
            // 缓存未注册属配置错误，已吊销凭证将残留至缓存 TTL 过期，告警暴露
            log.warn("凭证缓存未注册，跳过精确清除，用户 ID：{}", userId);
            return;
        }

        for (String token : tokens) {
            cache.evict(token);
        }
    }

    /**
     * 通过令牌查找有效用户认证凭证
     *
     * <h3>处理逻辑
     * <p>构建查询条件：令牌匹配且未过期。
     * <p>从数据库查询符合条件的凭证记录。
     *
     * <h3>缓存策略
     * <p>使用 @Cacheable 注解，首次查询时从数据库获取数据并缓存，后续查询直接从缓存返回。
     * <p>缓存名称为 user:credential:token，键为令牌，Redis 键格式为：`user:credential:token:xxx`。
     * <p>不缓存空值：令牌为 32 位随机串不可枚举，穿透风险可忽略。
     *
     * @param token 认证令牌，不能为空
     * @return 用户认证凭证实体对象，未找到或已过期时返回 null
     */
    @Cacheable(value = UserCredentialCacheTtlCustomizer.CACHE_USER_CREDENTIAL_TOKEN, key = "#token")
    @LogExecution
    public UserCredential findValidByToken(@NotBlank String token) {
        return userCredentialMapper.selectOneByCondition(
            USER_CREDENTIAL.TOKEN.eq(token)
                .and(USER_CREDENTIAL.EXPIRE_TIME.gt(Instant.now()))
        );
    }
}
