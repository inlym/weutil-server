create table `user_credential`
(
    -- ================================ 通用字段 ================================
    `id`                bigint unsigned not null auto_increment comment '主键 ID',
    `create_time`       timestamp(6)    not null default current_timestamp(6) comment '创建时间',
    `update_time`       timestamp(6)    not null default current_timestamp(6) on update current_timestamp(6) comment '更新时间',

    -- ================================ 关联字段 ================================
    `user_id`           bigint unsigned not null comment '用户 ID',

    -- ================================ 业务字段 ================================
    `token`             char(32)        not null comment '认证令牌',
    `expire_time`       timestamp(6)    null default null comment '过期时间',
    `renewal_count`     int unsigned    not null default 0 comment '已续期次数',
    `last_renewal_time` timestamp(6)    null default null comment '上一次续期时间',

    primary key (`id`),
    unique key `uk_token` (`token`)
) engine = InnoDB default character set = `utf8mb4` comment = '用户认证凭证';
