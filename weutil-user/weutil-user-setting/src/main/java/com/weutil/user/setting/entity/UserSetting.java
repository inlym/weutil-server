package com.weutil.user.setting.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 用户设置实体类
 *
 * <h2>说明
 * <p>存储用户各项偏好设置，采用 key-value 结构，每个用户每个设置项最多一条记录。
 * <p>未设置时表中无记录，表示使用系统默认值。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_setting")
public class UserSetting {

    // ================================ 通用字段 ================================

    /** 主键 ID */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 创建时间 */
    private Instant createTime;

    /** 更新时间 */
    private Instant updateTime;

    // ================================ 关联字段 ================================

    /**
     * 用户 ID
     *
     * <h3>字段说明
     * <p>设置项关联的用户 ID
     * <p>用于将设置项与具体用户进行关联
     *
     * <h3>数据库字段定义
     * <p>数据类型：bigint unsigned
     * <p>非空约束：NOT NULL
     * <p>默认值：无
     * <p>字段约束：与 setting_key 组成唯一索引
     */
    private Long userId;

    // ================================ 业务字段 ================================

    /**
     * 设置项键名
     *
     * <h3>字段说明
     * <p>标识具体的设置项类型
     * <p>合法值由各模块的 UserSettingDefinition 实现约束
     *
     * <h3>数据库字段定义
     * <p>数据类型：varchar(50)
     * <p>非空约束：NOT NULL
     * <p>默认值：无
     * <p>字段约束：与 user_id 组成唯一索引
     */
    private String settingKey;

    /**
     * 设置项值
     *
     * <h3>字段说明
     * <p>设置项的实际值，统一以字符串形式存储
     * <p>具体值的含义和格式由 setting_key 决定
     *
     * <h3>数据库字段定义
     * <p>数据类型：varchar(500)
     * <p>非空约束：NOT NULL
     * <p>默认值：无
     */
    private String settingValue;
}
