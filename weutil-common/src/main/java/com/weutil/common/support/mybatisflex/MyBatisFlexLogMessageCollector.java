package com.weutil.common.support.mybatisflex;

import com.mybatisflex.core.audit.AuditMessage;
import com.mybatisflex.core.audit.MessageCollector;
import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis-Flex SQL 审计日志收集器
 *
 * <h2>功能说明
 * <p>实现 MyBatis-Flex 的 MessageCollector 接口
 * <p>收集 SQL 执行信息并输出到日志，包括执行时间和完整 SQL 语句
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
public class MyBatisFlexLogMessageCollector implements MessageCollector {

    // ================================ public 方法 ================================

    @Override
    public void collect(AuditMessage message) {
        String formattedSql = formatSql(message.getFullSql());
        String countMessage = formatQueryCountMessage(message);
        log.info("[SQL] [{}ms] {} {}", message.getElapsedTime(), countMessage, formattedSql);
    }

    // ================================ private 方法 ================================

    /**
     * 格式化查询行数消息
     *
     * <h3>处理规则
     * <p>根据 SQL 类型返回不同的文案描述
     *
     * @param message 审计消息
     * @return 格式化后的行数消息
     */
    private String formatQueryCountMessage(AuditMessage message) {
        String sql = message.getQuery().trim().toUpperCase();
        int count = message.getQueryCount();

        if (sql.startsWith("SELECT")) {
            return String.format("[返回%d行]", count);
        } else if (sql.startsWith("INSERT")) {
            return String.format("[插入%d行]", count);
        } else if (sql.startsWith("UPDATE")) {
            return String.format("[更新%d行]", count);
        } else if (sql.startsWith("DELETE")) {
            return String.format("[删除%d行]", count);
        } else {
            return String.format("[影响%d行]", count);
        }
    }

    /**
     * 格式化 SQL 语句
     *
     * <h3>处理规则
     * <p>将所有换行符替换为 ↩︎ 符号，使 SQL 语句在日志中单行显示
     *
     * @param sql 原始 SQL 语句
     * @return 格式化后的 SQL 语句
     */
    private String formatSql(String sql) {
        return sql.replaceAll("\\r?\\n", "↩︎");
    }
}
