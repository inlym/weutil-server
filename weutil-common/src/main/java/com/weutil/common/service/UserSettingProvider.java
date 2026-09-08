package com.weutil.common.service;

import com.weutil.common.extension.UserSettingDefinition;

/**
 * 用户设置读写接口
 *
 * <h2>说明
 * <p>提供用户设置的读取和写入能力，由 user-setting 模块实现。
 * <p>其他模块通过注入此接口使用设置功能，无需直接依赖 user-setting 模块。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public interface UserSettingProvider {

    /**
     * 获取用户设置值
     *
     * <h3>处理逻辑
     * <p>查询数据库中用户的设置记录，未找到时返回 {@code UserSettingDefinition.getDefaultValue()}。
     *
     * @param userId     用户 ID
     * @param definition 设置项定义
     * @return 设置值，不为 null
     */
    String getSetting(long userId, UserSettingDefinition definition);

    /**
     * 设置用户设置值
     *
     * <h3>处理逻辑
     * <p>用户首次设置时创建记录，后续修改在已有记录上更新。
     *
     * @param userId     用户 ID
     * @param definition 设置项定义
     * @param value      设置值
     */
    void setSetting(long userId, UserSettingDefinition definition, String value);
}
