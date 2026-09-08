package com.weutil.user.credential.service;

import com.weutil.user.credential.config.UserCredentialCacheTtlCustomizer;
import com.weutil.user.credential.entity.UserCredential;
import com.weutil.user.credential.mapper.UserCredentialMapper;
import com.weutil.common.annotation.LogExecution;
import com.weutil.common.util.RandomUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.Instant;

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

    // ================================ public 方法 ================================

    /**
     * 创建用户认证凭证
     *
     * <h3>处理逻辑
     * <p>生成指定位数的随机字母数字字符串作为认证令牌。
     * <p>计算凭证过期时间为当前时间加上默认有效期。
     * <p>构建用户认证凭证实体对象并存入数据库。
     *
     * @param userId   用户 ID
     * @param clientIp 客户端 IP
     * @return 创建后的用户认证凭证，不为 null
     */
    @LogExecution
    public UserCredential create(Long userId, String clientIp) {
        // 生成随机令牌并计算过期时间
        String token = RandomUtils.generateAlphanumeric(TOKEN_LENGTH);
        Instant expireTime = Instant.now().plus(DEFAULT_VALIDITY_PERIOD);

        // 构建凭证实体并持久化
        UserCredential credential = UserCredential
            .builder()
            .userId(userId)
            .token(token)
            .clientIp(clientIp)
            .expireTime(expireTime)
            .renewalCount(0)
            .build();

        userCredentialMapper.insertSelective(credential);

        log.info("创建用户认证凭证，ID：{}，用户 ID：{}，客户端 IP：{}", credential.getId(), userId, clientIp);

        return credential;
    }

    /**
     * 续期用户认证凭证
     *
     * <h3>处理逻辑
     * <p>重新计算过期时间为当前时间加上默认有效期。
     * <p>同时更新续期次数和上次续期时间字段。
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
        UserCredential updateCredential = UserCredential
            .builder()
            .id(credential.getId())
            .expireTime(now.plus(DEFAULT_VALIDITY_PERIOD))
            .renewalCount(credential.getRenewalCount() + 1)
            .lastRenewalTime(now)
            .build();

        userCredentialMapper.update(updateCredential);

        log.info(
            "续期用户认证凭证，ID：{}，用户 ID：{}，续期次数：{}",
            credential.getId(),
            credential.getUserId(),
            updateCredential.getRenewalCount()
        );
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
     * <p>缓存空值，防止缓存穿透攻击。
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
