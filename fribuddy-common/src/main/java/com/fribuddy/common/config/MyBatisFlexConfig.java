package com.fribuddy.common.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.logicdelete.impl.DateTimeLogicDeleteProcessor;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.fribuddy.common.support.mybatisflex.MyBatisFlexLogMessageCollector;
import com.fribuddy.common.support.mybatisflex.MyBatisFlexMessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * MyBatis-Flex 框架配置类
 *
 * <h2>功能说明
 * <p>配置 MyBatis-Flex ORM 框架的核心功能，包括逻辑删除、SQL 审计等。
 * <p>使用 DateTime 作为逻辑删除字段值，自动填充当前时间戳。
 * <p>通过自定义 MessageFactory 创建审计消息，注入平台标识和主机 IP。
 * <p>通过自定义 MessageCollector 收集 SQL 执行日志，记录执行时间和完整 SQL 语句。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
@RequiredArgsConstructor
public class MyBatisFlexConfig implements MyBatisFlexCustomizer {

    /** Spring 环境变量，用于获取应用名称 */
    private final Environment environment;

    // ================================ public 方法 ================================

    /**
     * 创建逻辑删除处理器
     *
     * <h3>配置说明
     * <p>使用 DateTimeLogicDeleteProcessor，逻辑删除时自动填充当前时间戳。
     * <p>查询时自动过滤已删除的数据（delete_time 不为 null）。
     *
     * @return 逻辑删除处理器实例
     */
    @Bean
    public LogicDeleteProcessor logicDeleteProcessor() {
        return new DateTimeLogicDeleteProcessor();
    }

    /**
     * 自定义 MyBatis-Flex 全局配置
     *
     * <h3>配置项
     * <p>设置自定义消息工厂，为审计消息注入应用名称和主机 IP。
     * <p>启用 SQL 审计功能，记录所有 SQL 执行日志。
     * <p>设置自定义日志收集器，将 SQL 执行信息输出到 Slf4j。
     * <p>关闭控制台 Banner 打印。
     *
     * @param config 全局配置对象
     */
    @Override
    public void customize(FlexGlobalConfig config) {
        // 从环境变量获取应用名称，用于在审计消息中标识 SQL 来源应用
        String platform = environment.getProperty("spring.application.name", "fribuddy-server");
        AuditManager.setMessageFactory(new MyBatisFlexMessageFactory(platform));

        // SQL 审计日志统一输出到 Slf4j，便于集中采集和问题排查
        AuditManager.setMessageCollector(new MyBatisFlexLogMessageCollector());

        // 开启 SQL 审计功能，记录每条 SQL 的执行耗时和影响行数
        AuditManager.setAuditEnable(true);

        // 关闭控制台 Banner，避免在启动日志中冗余打印
        config.setPrintBanner(false);
    }
}
