# CLAUDE.md

## 项目目标

weutil-server 是「小鸣助手」项目的服务端。

**技术栈**: Spring Boot 4.1.0 + Java 25 + MySQL + MyBatis-Flex 1.11.7 + Redis + Maven

**模块结构**:
```
weutil-server/
├── weutil-common/                 # 通用模块（全局配置、基础设施）
├── weutil-system/                 # 系统运行模块（与业务无关的系统级功能）
└── weutil-bootstrap/              # 启动模块（主类、端口 35125、profile: local）
```

**启动**: `cd weutil-bootstrap && mvn spring-boot:run`（需先在 application.yml 中填入数据库和 Redis 连接信息）
**调试**: `lsof -i :35125` → `kill -9 <PID>`

---

## 协作原则

以第一性原理，从原始需求和问题本质出发，不从惯例或模板出发。

1. 动机或目标不清晰时，停下来讨论，不要假设。
2. 路径不是最短的，直接说并建议更好的办法。
3. 遇到问题追根因，不打补丁。每个决策都要能回答"为什么"。
4. 输出说重点，砍掉一切不改变决策的信息。
5. 修改优先于重写，不引入超出任务范围的抽象。
6. 不为假设的未来需求设计，三行相似代码优于过早抽象。

---

## 规则索引

### 命名规范
- Java 命名约定、数据模型后缀、ID 注释用词、memory 术语翻译、注释禁用项目名与产品名
  → `.claude/rules/naming/java-naming.md`
- 实体类字段 JavaDoc（摘要行、`<h3>字段说明` 格式、数据库字段定义块）
  → `.claude/rules/naming/entity-javadoc.md`
- 普通模型类字段 JavaDoc（摘要行、`<h3>字段说明` 格式）
  → `.claude/rules/naming/model-javadoc.md`
- DTO/VO 字段 JavaDoc、控制器类与方法注释（Apifox Helper，优先级最高）
  → `.claude/rules/naming/dto-vo-javadoc.md`
- 字段注释规范入口（索引，指向实体类、普通模型类和 DTO/VO 三套规则）
  → `.claude/rules/naming/javadoc-style.md`

### 后端规范
- Spring 分层规范（Controller/Service/DI/Config 类/配置/查询命名契约/路径规范）
  → `.claude/rules/backend/spring-layering.md`
- Java 代码风格（禁止项、Lombok、方法排序与调用约束、换行规则含场景示例、字段与方法 JavaDoc、方法内部注释、RestClient）
  → `.claude/rules/backend/java-code-style.md`
- 异常处理（禁 try-catch、catch 只做转换、消息规范）
  → `.claude/rules/backend/exception-handling.md`
- 日志规范（级别分类、@Slf4j、禁记敏感信息、提前退出必须打 TRACE）
  → `.claude/rules/backend/logging.md`
- AbstractQueueProcessor 子类字段和方法分组规范
  → `.claude/rules/backend/queue-processor.md`
- 大模型调用服务规范（服务类封装、模型类命名与目录）
  → `.claude/rules/backend/llm-service.md`
- 配置文件格式（application.yml 分组注释、空行、注释规范）
  → `.claude/rules/backend/config-format.md`

### 数据库规范
- MySQL 建表标准（字段默认值原则、建表格式、字段类型映射）、MyBatis-Flex 使用规范
  → `.claude/rules/database/mysql.md`

### API 规范
- 业务错误码与 HTTP 状态码的区分规范
  → `.claude/rules/api/response.md`

### Git 规范
- AI 操作约束（禁止未授权的 commit/push/分支操作）
  → `.claude/rules/git/git-constraints.md`
