package com.weutil.common.config;

import com.weutil.common.support.mybatisflex.MyBatisFlexLogMessageCollector;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.logicdelete.impl.DateTimeLogicDeleteProcessor;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex 框架配置类
 *
 * <h2>功能说明
 * <p>配置 MyBatis-Flex ORM 框架的核心功能，包括逻辑删除、SQL 审计等
 * <p>使用 DateTime 作为逻辑删除字段值，自动填充当前时间戳
 * <p>通过自定义 MessageCollector 收集 SQL 执行日志，记录执行时间和完整 SQL 语句
 * <p>官方文档：<a href="https://mybatis-flex.com/">MyBatis-Flex</a>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
public class MyBatisFlexConfig implements MyBatisFlexCustomizer {

    // ================================ public 方法 ================================

    /**
     * 配置逻辑删除处理器
     *
     * <h3>配置说明
     * <p>使用 DateTimeLogicDeleteProcessor，逻辑删除时自动填充当前时间戳
     * <p>查询时自动过滤已删除的数据（delete_time 不为 null）
     *
     * @return 逻辑删除处理器实例
     * @see <a href="https://mybatis-flex.com/zh/core/logic-delete.html">逻辑删除</a>
     */
    @Bean
    public LogicDeleteProcessor logicDeleteProcessor() {
        return new DateTimeLogicDeleteProcessor();
    }

    /**
     * 自定义 MyBatis-Flex 全局配置
     *
     * <h3>配置项
     * <p>启用 SQL 审计功能，记录所有 SQL 执行日志
     * <p>设置自定义日志收集器，输出 SQL 执行时间和完整语句
     * <p>关闭控制台 Banner 打印
     *
     * @param config 全局配置对象
     * @see <a href="https://mybatis-flex.com/zh/base/mybatis-flex-customizer.html">MyBatisFlexCustomizer</a>
     */
    @Override
    public void customize(FlexGlobalConfig config) {
        // 设置自定义的 SQL 审计日志收集器
        AuditManager.setMessageCollector(new MyBatisFlexLogMessageCollector());

        // 开启审计功能
        AuditManager.setAuditEnable(true);

        // 关闭控制台打印标志
        config.setPrintBanner(false);
    }
}
