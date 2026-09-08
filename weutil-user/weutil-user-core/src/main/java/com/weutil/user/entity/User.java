package com.weutil.user.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.weutil.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 用户实体类
 *
 * <h2>说明
 * <p>表示系统中的用户基本信息，包含用户的个人资料和账户相关信息。
 * <p>用于用户身份识别、个人信息管理和用户行为追踪。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class User {

    // ================================ 通用字段 ================================

    /** 主键 ID */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 创建时间 */
    private Instant createTime;

    /** 更新时间 */
    private Instant updateTime;

    // ================================ 业务字段 ================================

    /**
     * 昵称
     *
     * <h3>字段说明
     * <p>用户的显示昵称，用于在系统中展示和识别用户
     * <p>可以与真实姓名不同，允许用户自定义个性化名称
     *
     * <h3>数据库字段定义
     * <p>数据类型：varchar(100)
     * <p>非空约束：NOT NULL
     * <p>默认值：无
     */
    private String nickname;

    /**
     * 头像在对象存储中存储的键名
     *
     * <h3>字段说明
     * <p>用于标识用户头像在对象存储服务中的存储位置
     * <p>为空时表示用户未设置头像
     *
     * <h3>数据库字段定义
     * <p>数据类型：varchar(50)
     * <p>非空约束：NULL
     * <p>默认值：NULL
     */
    private String avatarKey;

    /**
     * 注册时间
     *
     * <h3>字段说明
     * <p>用户在系统中完成注册的具体时间点
     * <p>用于用户生命周期管理和注册数据分析
     *
     * <h3>数据库字段定义
     * <p>数据类型：timestamp(6)
     * <p>非空约束：NULL
     * <p>默认值：NULL
     */
    private Instant registerTime;

    /**
     * 最后一次登录时间
     *
     * <h3>字段说明
     * <p>用户最后一次成功登录系统的时间戳
     * <p>用于用户活跃度分析和安全监控
     *
     * <h3>数据库字段定义
     * <p>数据类型：timestamp(6)
     * <p>非空约束：NULL
     * <p>默认值：NULL
     */
    private Instant lastLoginTime;

    /**
     * 用户状态
     *
     * <h3>字段说明
     * <p>表示用户的当前状态，用于用户访问控制和安全管理
     * <p>正常状态用户可以正常使用系统，锁定状态用户无法登录和使用功能
     *
     * <h3>数据库字段定义
     * <p>数据类型：int unsigned
     * <p>非空约束：NOT NULL
     * <p>默认值：0
     */
    private UserStatus status;
}
