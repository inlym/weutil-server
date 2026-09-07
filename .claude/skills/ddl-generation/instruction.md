# DDL 生成执行指令

## 重要提示

当用户表达需要生成或更新建表语句时，严格按照以下步骤执行。所有操作必须遵循 `.claude/rules/database/mysql.md` 中的建表规范。

---

## 执行步骤

### 第一步：遍历实体类

遍历项目，寻找所有使用 `com.mybatisflex.annotation.Table` 注解的类文件。

---

### 第二步：生成建表语句

按照 `.claude/rules/database/mysql.md` 中"实体类到 SQL 映射规范"和"建表格式规范"，为每个实体类生成对应的 CREATE TABLE DDL 语句。

---

### 第三步：保存建表语句

1. 每个实体类的建表语句保存为单独的 `.sql` 文件，文件名为表名
2. 文件按包名归入 `DevOps/mysql/` 下对应的子目录（目录映射见 `mysql.md` 中"SQL 文件管理"章节）
3. 表结构未发生变化时直接跳过

---

### 第四步：复核

检查以下要点：

1. **可空 timestamp(6) 列**：是否显式写了 `null` 约束（`timestamp(6) null default null`）
2. **建表语句结构**：是否符合 `mysql.md` 中"建表格式规范"的示例
3. **默认值为"无"**：SQL 中是否正确省略了 DEFAULT 子句

---

### 第五步：汇总

将所有 SQL 文件中的脚本汇总到 `DevOps/mysql/tables.sql`。

---

## 执行原则

1. **增量更新**：未变更的表不重复生成
2. **规范优先**：所有 DDL 必须符合 `mysql.md` 中的规范
3. **完整流程**：严格按照上述五个步骤顺序执行，不得跳过
