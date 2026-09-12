package com.fribuddy.user.credential.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 用户认证凭证
 *
 * <h2>说明
 * <p>存储用户身份认证的凭证信息，包含用于用户 API 请求鉴权的令牌数据。
 * <p>用于用户身份验证、访问权限控制和会话管理。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_credential")
public class UserCredential {

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
     * <p>凭证关联的用户 ID
     * <p>用于将凭证与具体用户进行关联
     *
     * <h3>数据库字段定义
     * <p>数据类型：bigint unsigned
     * <p>非空约束：NOT NULL
     * <p>默认值：无
     */
    private Long userId;

    // ================================ 业务字段 ================================

    /**
     * 认证令牌
     *
     * <h3>字段说明
     * <p>客户端发起 API 请求时携带该内容用于表示用户身份
     * <p>用于服务端验证用户身份和访问权限
     *
     * <h3>数据库字段定义
     * <p>数据类型：char(32)
     * <p>非空约束：NOT NULL
     * <p>默认值：无
     * <p>字段约束：唯一索引
     */
    private String token;

    /**
     * 过期时间
     *
     * <h3>字段说明
     * <p>凭证失效过期的时间点
     * <p>用于凭证有效性验证和自动过期处理
     *
     * <h3>数据库字段定义
     * <p>数据类型：timestamp(6)
     * <p>非空约束：NULL
     * <p>默认值：NULL
     */
    private Instant expireTime;

    /**
     * 已续期次数
     *
     * <h3>字段说明
     * <p>凭证被续期的累计次数
     * <p>用于限制续期次数和会话安全管理
     *
     * <h3>数据库字段定义
     * <p>数据类型：int unsigned
     * <p>非空约束：NOT NULL
     * <p>默认值：0
     */
    private Integer renewalCount;

    /**
     * 上一次续期时间
     *
     * <h3>字段说明
     * <p>凭证最近一次被续期的时间点
     * <p>用于续期频率控制和审计追踪
     *
     * <h3>数据库字段定义
     * <p>数据类型：timestamp(6)
     * <p>非空约束：NULL
     * <p>默认值：NULL
     */
    private Instant lastRenewalTime;
}
