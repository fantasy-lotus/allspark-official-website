# 创建数据库
create database if not exists allspark;
# 使用数据库
use allsaprk;

# TRUNCATE TABLE user;

# 创建表
create table user
(
    `user_id`     int auto_increment primary key comment '主键id',
    `user_name`   varchar(15) not null comment '名字',
    `qq_number`   varchar(15) not null unique comment 'QQ号',
    `grade`       tinyint(1)  not null comment '年级',
    `direction`   varchar(4)  not null comment '方向选择 前端 / 后端 / 产品',
    `progress`    text        null comment '学习进展',
    `create_time` timestamp   not null default current_timestamp comment '填写申请表的时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment '储存普通用户信息的表';
