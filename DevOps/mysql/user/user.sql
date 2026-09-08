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
