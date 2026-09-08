package com.weutil.system.echo.controller;

import com.weutil.common.annotation.LogExecution;
import com.weutil.common.constants.ContextKeys;
import com.weutil.system.echo.model.RequestEchoVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求回显控制器
 *
 * <h2>控制器说明
 * <p>提供 HTTP 请求信息回显接口，用于调试客户端发送的完整请求数据。
 *
 * @module 系统运维
 * @folder 请求回显
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@RestController
public class RequestEchoController {

    // ================================ public 方法 ================================

    /**
     * 获取请求详情并原样回显
     * 提取 HTTP 请求的方法、路径、查询字符串、客户端 IP、请求头、查询参数和请求体，封装后原样返回。
     * 接口不限定 HTTP 方法，便于回显任意方法发起的调试请求。
     *
     * @param request HTTP 请求对象
     * @param body    请求体内容，允许为空
     * @return 请求详情回显对象
     */
    @LogExecution
    @RequestMapping("/request-echoes")
    public RequestEchoVO echoRequest(HttpServletRequest request, @RequestBody(required = false) String body) {
        // 收集请求头，同名头可能出现多次，逐一收集所有值
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            List<String> headerValues = new ArrayList<>();
            Enumeration<String> values = request.getHeaders(headerName);
            while (values.hasMoreElements()) {
                headerValues.add(values.nextElement());
            }

            headers.put(headerName, headerValues);
        }

        // 从请求属性获取反向代理解析后的真实客户端 IP，dev 环境未启用解析时为 null
        String clientIp = (String) request.getAttribute(ContextKeys.CLIENT_IP);

        return RequestEchoVO
            .builder()
            .method(request.getMethod())
            .path(request.getRequestURI())
            .queryString(request.getQueryString())
            .remoteAddr(request.getRemoteAddr())
            .clientIp(clientIp)
            .headers(headers)
            .query(request.getParameterMap())
            .body(body)
            .build();
    }
}
