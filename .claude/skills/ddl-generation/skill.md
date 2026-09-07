# DDL 生成 Skill

## Description

根据实体类自动生成或更新 MySQL 建表（CREATE TABLE）DDL 语句，并汇总到统一的 SQL 脚本文件。

## Use Cases

- 新增实体类后生成对应建表语句
- 修改实体类字段后更新建表语句
- 需要重新生成所有建表语句

## Usage

当用户表达以下意图时，自动触发此 skill：
- "生成建表语句"
- "更新建表语句"
- "生成 DDL"
- "同步建表 SQL"
