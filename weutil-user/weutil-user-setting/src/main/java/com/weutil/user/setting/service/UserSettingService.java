package com.weutil.user.setting.service;

import com.weutil.user.setting.entity.UserSetting;
import com.weutil.user.setting.mapper.UserSettingMapper;
import com.weutil.common.extension.UserSettingDefinition;
import com.weutil.common.service.UserSettingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.weutil.user.setting.entity.table.UserSettingTableDef.USER_SETTING;

/**
 * 用户设置服务
 *
 * <h2>说明
 * <p>实现 {@code UserSettingProvider} 接口，提供用户设置的读取和写入能力。
 * <p>读取时优先查询数据库，未找到记录时返回设置项的默认值。
 * <p>写入时若记录不存在则创建，已存在则更新。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSettingService implements UserSettingProvider {

    /** 用户设置数据访问层 */
    private final UserSettingMapper userSettingMapper;

    // ================================ public 方法 ================================

    /**
     * 获取用户设置值
     *
     * @param userId     用户 ID
     * @param definition 设置项定义
     * @return 设置值，不为 null
     */
    @Override
    public String getSetting(long userId, UserSettingDefinition definition) {
        // 查询用户设置记录
        UserSetting setting = userSettingMapper.selectOneByCondition(
            USER_SETTING.USER_ID.eq(userId)
                .and(USER_SETTING.SETTING_KEY.eq(definition.getKey()))
        );

        if (setting != null) {
            return setting.getSettingValue();
        }

        log.trace("用户设置不存在，返回默认值，userId={}，key={}", userId, definition.getKey());
        return definition.getDefaultValue();
    }

    /**
     * 设置用户设置值
     *
     * @param userId     用户 ID
     * @param definition 设置项定义
     * @param value      设置值
     */
    @Override
    public void setSetting(long userId, UserSettingDefinition definition, String value) {
        // 查询是否已有设置记录
        UserSetting existing = userSettingMapper.selectOneByCondition(
            USER_SETTING.USER_ID.eq(userId)
                .and(USER_SETTING.SETTING_KEY.eq(definition.getKey()))
        );

        if (existing != null) {
            // 已有记录则更新设置值
            UserSetting updateSetting = UserSetting
                .builder()
                .id(existing.getId())
                .settingValue(value)
                .build();
            userSettingMapper.update(updateSetting);
        } else {
            // 不存在则新建设置记录
            UserSetting newSetting = UserSetting
                .builder()
                .userId(userId)
                .settingKey(definition.getKey())
                .settingValue(value)
                .build();
            userSettingMapper.insertSelective(newSetting);
        }
    }
}
