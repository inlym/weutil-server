package com.fribuddy.user.credential.config;

import com.fribuddy.common.extension.CacheTtlCustomizer;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证凭证缓存 TTL 配置定制器
 *
 * <h2>说明
 * <p>配置用户认证凭证缓存的有效期。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
public class UserCredentialCacheTtlCustomizer implements CacheTtlCustomizer {

    // ================================ 静态常量字段 ================================

    /** 用户认证凭证缓存：按令牌查询 */
    public static final String CACHE_USER_CREDENTIAL_TOKEN = "user:credential:token";

    // ================================ public 方法 ================================

    /**
     * 声明缓存 TTL 配置
     *
     * <h3>缓存有效期说明
     * <ul>
     *   <li>user:credential:token：2 小时，凭证有效期 30 天（剩余 10 天内自动续期）</li>
     *   <li>缓存过期时凭证可能仍有效，缓存命中期间过期时点最多放宽 2 小时</li>
     * </ul>
     *
     * @return 缓存名称与有效期的映射
     */
    @Override
    public Map<String, Duration> declareCacheTtl() {
        Map<String, Duration> config = new HashMap<>();
        config.put(CACHE_USER_CREDENTIAL_TOKEN, Duration.ofHours(2));
        return config;
    }
}
