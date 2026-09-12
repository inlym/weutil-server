package com.fribuddy.common.support.mybatisflex;

import com.mybatisflex.core.audit.AuditMessage;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis-Flex SQL 审计日志收集器
 *
 * <h2>功能说明
 * <p>将 SQL 执行信息以结构化单行格式输出到 Slf4j，便于日志平台采集、检索和慢查询告警。
 *
 * <h2>输出格式
 * <p>{@code [SQL] <elapsed>ms rows=<n> ds=<name> user=<user> url=<url> | <sql>}
 * <p>耗时和行数始终输出；数据源名称始终输出；用户和 URL 仅在有值时输出。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
public class MyBatisFlexLogMessageCollector implements MessageCollector {

    // ================================ public 方法 ================================

    /**
     * 收集并输出 SQL 审计日志
     *
     * <h3>输出格式
     * <p>格式为 [SQL] 耗时ms rows=行数 ds=数据源 user=用户 url=请求路径 | 完整SQL。
     * <p>SQL 内的换行符替换为 ↩︎ 符号以保持单行输出。
     *
     * @param message 审计消息，包含 SQL 语句、执行耗时、影响行数和上下文信息
     */
    @Override
    public void collect(AuditMessage message) {
        log.info("{}", buildLogLine(message));
    }

    // ================================ private 方法 ================================

    /**
     * 构建日志行
     *
     * <h3>构建策略
     * <p>耗时放最前面便于慢查询排查，元数据以 key=value 形式输出便于日志平台解析，SQL 放在 | 之后避免元数据被淹没。
     * <p>可选字段（user、url）仅在 AuditMessage 中有值时追加，避免输出无意义的空值。
     *
     * @param message 审计消息
     * @return 结构化日志行
     */
    private String buildLogLine(AuditMessage message) {
        StringBuilder line = new StringBuilder(256);

        // 标签头，固定以 [SQL] 开头便于 grep 过滤
        line.append("[SQL] ");

        // 耗时放在最前面，慢查询排查时最先看到
        line.append(message.getElapsedTime()).append("ms ");

        // 行数用于识别批量操作和意外的行数变化
        line.append("rows=").append(message.getQueryCount()).append(" ");

        // 数据源名称，多数据源场景下必不可少
        line.append("ds=").append(getDsName(message)).append(" ");

        // 以下为可选上下文，仅在 AuditMessage 中有值时追加，避免输出无意义的空值
        appendIfPresent(line, "user", message.getUser());
        appendIfPresent(line, "url", message.getUrl());

        // | 作为元数据与 SQL 的分界，SQL 中极少出现此字符
        line.append("| ");

        // SQL 转单行后追加，null 时输出占位符
        line.append(toSingleLine(message.getFullSql()));

        return line.toString();
    }

    /**
     * 追加可选字段
     *
     * <h3>追加规则
     * <p>仅当值非空时追加，key 和 value 之间用 = 连接，末尾追加一个空格与后续字段分隔。
     *
     * @param line  日志行构建器
     * @param key   字段名
     * @param value 字段值
     */
    private void appendIfPresent(StringBuilder line, String key, String value) {
        if (StringUtil.hasText(value)) {
            line.append(key).append('=').append(value).append(' ');
        }
    }

    /**
     * 获取数据源名称
     *
     * <h3>兜底策略
     * <p>AuditMessage.dsName 在单数据源时可能为空，此时使用 "default" 作为默认值。
     *
     * @param message 审计消息
     * @return 数据源名称
     */
    private String getDsName(AuditMessage message) {
        return StringUtil.hasText(message.getDsName()) ? message.getDsName() : "default";
    }

    /**
     * 将 SQL 转为单行
     *
     * <h3>处理规则
     * <p>将换行符替换为 ↩︎ 符号，使 SQL 在日志中保持单行，避免打断日志采集管道的行解析。
     *
     * @param sql 原始 SQL 语句
     * @return 单行 SQL，null 时返回 "-"
     */
    private String toSingleLine(String sql) {
        if (sql == null) {
            return "-";
        }

        // 逐字面量替换，避免正则表达式编译开销
        return sql.replace("\r\n", "↩︎").replace("\n", "↩︎").replace("\r", "↩︎");
    }
}
