package com.weutil.common.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.DefaultMessageFactory;
import com.mybatisflex.core.audit.MessageFactory;
import com.mybatisflex.core.logicdelete.impl.DateTimeLogicDeleteProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * MyBatis-Flex 全局配置单元测试
 *
 * <h2>说明
 * <p>直接实例化 MyBatisFlexConfig 验证其配置逻辑，不依赖 Spring 容器。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
class MyBatisFlexConfigTest {

    /** 模拟的 Spring 环境变量 */
    private final Environment environment = mock(Environment.class);

    /** 被测试的配置实例 */
    private final MyBatisFlexConfig config = new MyBatisFlexConfig(environment);

    // ================================ 生命周期 ================================

    /** 初始化环境变量模拟行为 */
    @BeforeEach
    void setUp() {
        when(environment.getProperty("spring.application.name", "weutil-server"))
            .thenReturn("test-app");
    }

    /** 每个测试后关闭审计功能并重置消息工厂，避免状态污染 */
    @AfterEach
    void tearDown() {
        AuditManager.setAuditEnable(false);
        AuditManager.setMessageFactory(new DefaultMessageFactory());
    }

    // ================================ 测试方法 ================================

    /**
     * 逻辑删除处理器为 DateTime 类型
     *
     * <h3>验证配置
     * <p>logicDeleteProcessor() 返回 DateTimeLogicDeleteProcessor 实例，逻辑删除时自动填充当前时间戳。
     */
    @Test
    void logicDeleteProcessorIsDateTimeType() {
        assertThat(config.logicDeleteProcessor()).isInstanceOf(DateTimeLogicDeleteProcessor.class);
    }

    /**
     * customize 启用审计、设置消息工厂并关闭 Banner
     *
     * <h3>验证配置
     * <p>调用 customize 后，AuditManager 审计开关开启，MessageFactory 可正常创建审计消息，FlexGlobalConfig 的 printBanner 为 false。
     */
    @Test
    void customizeEnablesAuditAndDisablesBanner() {
        FlexGlobalConfig globalConfig = new FlexGlobalConfig();

        // 执行自定义配置
        config.customize(globalConfig);

        // 验证审计功能已开启
        assertThat(AuditManager.isAuditEnable()).isTrue();

        // 验证消息工厂已设置，且创建的审计消息包含正确的平台标识
        MessageFactory messageFactory = AuditManager.getMessageFactory();
        assertThat(messageFactory).isNotNull();
        assertThat(messageFactory.create().getPlatform()).isEqualTo("test-app");

        // 验证 Banner 已关闭
        assertThat(globalConfig.isPrintBanner()).isFalse();
    }
}
