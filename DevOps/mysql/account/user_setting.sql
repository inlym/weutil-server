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
