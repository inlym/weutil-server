-- ================================================
-- 数据库建表脚本（自动生成）
-- ================================================

create table `user`
(
    -- ================================ 通用字段 ================================
    `id`              bigint unsigned not null auto_increment comment '主键 ID',
    `create_time`     timestamp(6)    not null default current_timestamp(6) comment '创建时间',
    `update_time`     timestamp(6)    not null default current_timestamp(6) on update current_timestamp(6) comment '更新时间',

    -- ================================ 业务字段 ================================
    `nickname`        varchar(100)    not null comment '昵称',
    `avatar_key`      varchar(50)     default null comment '头像在对象存储中存储的键名',
    `register_time`   timestamp(6)    null default null comment '注册时间',
    `last_login_time` timestamp(6)    null default null comment '最后一次登录时间',
    `status`          int unsigned    not null default 0 comment '用户状态',

    primary key (`id`)
) engine = InnoDB default character set = `utf8mb4` comment = '用户实体类';

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
    `client_ip`         varchar(50)     default null comment '客户端 IP',
    `renewal_count`     int unsigned    not null default 0 comment '已续期次数',
    `last_renewal_time` timestamp(6)    null default null comment '上一次续期时间',

    primary key (`id`),
    unique key `uk_token` (`token`)
) engine = InnoDB default character set = `utf8mb4` comment = '用户认证凭证实体类';

create table `user_setting`
(
    -- ================================ 通用字段 ================================
    `id`            bigint unsigned not null auto_increment comment '主键 ID',
    `create_time`   timestamp(6)    not null default current_timestamp(6) comment '创建时间',
    `update_time`   timestamp(6)    not null default current_timestamp(6) on update current_timestamp(6) comment '更新时间',

    -- ================================ 关联字段 ================================
    `user_id`       bigint unsigned not null comment '用户 ID',

    -- ================================ 业务字段 ================================
    `setting_key`   varchar(50)     not null comment '设置项键名',
    `setting_value` varchar(500)    not null comment '设置项值',

    primary key (`id`),
    unique key `uk_user_key` (`user_id`, `setting_key`)
) engine = InnoDB default character set = `utf8mb4` comment = '用户设置实体类';
