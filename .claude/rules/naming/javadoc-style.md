# JavaDoc 字段注释规范

字段注释分两大类，规则不同：

- 数据类字段（实体类、普通模型类、DTO/VO）→ 见下方类型判断表
- Spring Bean 字段（Service、Component、Configuration 等）→ `.claude/rules/backend/java-code-style.md`（字段注释章节）

## 类型判断

| 类型 | 判断依据 | 适用规范 |
|---|---|---|
| 实体类 | 带 `@Table` 注解 | `entity-javadoc.md` |
| 普通模型类 | 无 `@Table`，类名不以 `DTO`/`VO` 结尾（含 `@ConfigurationProperties` 类） | `model-javadoc.md` |
| DTO/VO | 类名以 `DTO` 或 `VO` 结尾 | `dto-vo-javadoc.md` |
