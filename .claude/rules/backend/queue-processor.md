# AbstractQueueProcessor 子类规范

子类字段和方法必须按以下分组顺序排列，仅包含存在内容的分组。

## 字段分组顺序

| 顺序 | 分组标题 | 定义 |
|---|---|---|
| 1 | 静态常量字段 | `static final` 修饰 |
| 2 | 可设置字段 | 外部通过 `@Setter` 或公开方法配置 |
| 3 | 依赖注入字段 | `private final`，由 Spring 注入 |
| 4 | 实例状态字段 | 其余字段 |

字段分组顺序优先于访问级别排序。基类 `AbstractQueueProcessor` 不受此规范约束。

## 方法分组顺序

| 顺序 | 分组标题 | 定义 |
|---|---|---|
| 1 | public 方法 | `public` 修饰 |
| 2 | protected 重写方法 | `@Override` 的 `protected` 方法 |
| 3 | protected 可重写方法 | 非 `@Override` 的 `protected` 方法（钩子） |
| 4 | private 方法 | `private` 修饰 |

大多数子类只有 protected 重写方法，无需创建其他分组。
