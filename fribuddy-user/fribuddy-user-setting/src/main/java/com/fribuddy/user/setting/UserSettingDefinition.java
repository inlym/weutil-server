package com.fribuddy.user.setting;

/**
 * 用户设置项定义接口
 *
 * <h2>说明
 * <p>各模块通过枚举实现此接口，定义该模块内的用户设置项及其默认值。
 * <p>实现方模块需依赖本模块，设置值通过 {@code UserSettingService} 读写。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public interface UserSettingDefinition {

    /**
     * 设置项键名
     *
     * @return 键名，建议使用 {@code 模块前缀.设置名} 格式避免冲突
     */
    String getKey();

    /**
     * 设置项默认值
     *
     * @return 默认值，用户未设置时使用此值
     */
    String getDefaultValue();
}
