package com.weutil.common.support.mybatisflex;

import com.mybatisflex.core.audit.AuditMessage;
import com.mybatisflex.core.audit.MessageFactory;
import com.mybatisflex.core.audit.http.HttpUtil;

/**
 * MyBatis-Flex 审计消息工厂
 *
 * <h2>功能说明
 * <p>创建审计消息并填充基础上下文信息（平台标识、主机 IP 等），替代默认的 DefaultMessageFactory。
 * <p>平台标识取自 Spring 应用名称，便于在多应用环境中区分 SQL 日志来源。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class MyBatisFlexMessageFactory implements MessageFactory {

    /** 主机 IP 地址缓存，首次获取后复用 */
    private String hostIp;

    /** 平台标识，取自 Spring 应用名称 */
    private final String platform;

    /**
     * 创建审计消息工厂
     *
     * @param platform 平台标识，通常取 Spring 应用名称
     */
    public MyBatisFlexMessageFactory(String platform) {
        this.platform = platform;
    }

    // ================================ 接口实现 ================================

    /**
     * 创建审计消息
     *
     * <h3>处理逻辑
     * <p>创建 AuditMessage 实例并填充平台标识和主机 IP。
     *
     * @return 填充了基础上下文信息的审计消息
     */
    @Override
    public AuditMessage create() {
        AuditMessage message = new AuditMessage();

        // 填充平台标识，用于区分不同应用的 SQL 日志
        message.setPlatform(platform);

        // 填充主机 IP 用于定位执行 SQL 的服务器
        message.setHostIp(getHostIp());

        return message;
    }

    // ================================ private 方法 ================================

    /**
     * 获取当前主机 IP 地址
     *
     * <h3>缓存策略
     * <p>首次获取后缓存在 hostIp 字段中，后续请求直接返回缓存值。
     *
     * @return 主机 IP 地址
     */
    private String getHostIp() {
        if (hostIp == null) {
            hostIp = HttpUtil.getHostIp();
        }

        return hostIp;
    }
}
