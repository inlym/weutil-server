package com.weutil.system.echo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 请求回显 VO
 *
 * <h2>类说明
 * <p>封装 HTTP 请求的完整信息，用于请求调试场景下原样回显客户端发送的请求详情。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEchoVO {

    /**
     * HTTP 请求方法
     *
     * @example GET
     */
    private String method;

    /**
     * HTTP 请求的路径部分，不含查询字符串
     *
     * @example /echo/request
     */
    private String path;

    /**
     * HTTP 请求的查询字符串，不含问号前缀
     *
     * @example key=value&foo=bar
     */
    private String queryString;

    /**
     * 发起请求的客户端 IP 地址
     *
     * @example 192.168.1.100
     */
    private String remoteAddr;

    /**
     * 经反向代理转发头解析后的真实客户端 IP 地址，未解析到时为 null
     *
     * @example 125.114.61.106
     */
    private String clientIp;

    /**
     * HTTP 请求的所有请求头，键为头名称，值为头内容
     *
     * @example {}
     */
    private Map<String, String> headers;

    /**
     * HTTP 请求的所有查询参数，键为参数名，值为参数值数组
     *
     * @example {}
     */
    private Map<String, String[]> query;

    /**
     * HTTP 请求体的原始文本内容
     *
     * @example {"key":"value"}
     */
    private String body;
}
