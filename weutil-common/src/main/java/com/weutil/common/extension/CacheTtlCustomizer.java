package com.weutil.common.extension;

import java.time.Duration;
import java.util.Map;

/**
 * 缓存 TTL 配置定制器接口
 *
 * <h2>说明
 * <p>业务模块可实现此接口，自定义缓存名称与有效期的映射配置。
 * <p>核心模块会自动收集所有实现类，统一注册到 RedisCacheManager。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public interface CacheTtlCustomizer {

    /**
     * 声明缓存 TTL 配置
     *
     * <h3>方法说明
     * <p>业务模块实现此方法，声明本模块所需的缓存名称与有效期映射。
     * <p>核心模块会自动收集所有声明，统一注册到 RedisCacheManager。
     *
     * @return 缓存名称与有效期的映射，不允许返回 null
     */
    Map<String, Duration> declareCacheTtl();
}