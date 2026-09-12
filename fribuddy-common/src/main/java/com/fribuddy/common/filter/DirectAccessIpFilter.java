package com.fribuddy.common.filter;

import com.fribuddy.common.constants.ContextKeys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 直接访问 IP 地址过滤器
 *
 * <h2>说明
 * <p>从直接访问（IP:端口）的请求中获取客户端真实 IP 地址。
 * <p>适用于通过 IP 地址和端口直接访问服务的场景。
 * <p>当请求通过 IP:端口直接访问（Host 包含冒号）时由本过滤器处理。
 *
 * <h2>功能特性
 * <ul>
 *   <li>直接通过 ServletRequest.getRemoteAddr() 获取客户端 IP 地址</li>
 *   <li>仅在客户端 IP 不为 null 且请求属性中为空时才赋值</li>
 *   <li>仅对包含冒号的 Host 请求头进行处理</li>
 * </ul>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Component
public class DirectAccessIpFilter extends OncePerRequestFilter implements Ordered {

    /**
     * 获取过滤器执行顺序
     *
     * <h3>执行顺序说明
     * <p>返回较高优先级，确保该过滤器在业务逻辑处理前执行
     * <p>在 TraceIdFilter 之后执行
     *
     * @return 过滤器执行顺序优先级值
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    /**
     * 判断是否需要跳过过滤处理
     *
     * <h3>跳过条件
     * <p>当 Host 请求头不存在或不包含冒号时跳过过滤处理
     * <p>Host 包含冒号表示直接通过 IP:端口方式访问
     *
     * @param request HTTP 请求对象
     * @return true 表示跳过过滤处理，false 表示执行过滤处理
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String host = request.getHeader("Host");
        return host == null || !host.contains(":");
    }

    /**
     * 执行过滤器内部逻辑
     *
     * <h3>处理流程
     * <p>1. 通过 getRemoteAddr() 方法获取客户端 IP 地址
     * <p>2. 仅在客户端 IP 不为 null 且请求属性中为空时才赋值
     * <p>3. 继续执行过滤器链
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException 处理请求时发生 Servlet 异常
     * @throws IOException      处理请求时发生 IO 异常
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();

        // 仅在客户端 IP 不为 null 且请求属性中为空时才赋值
        if (clientIp != null && request.getAttribute(ContextKeys.CLIENT_IP) == null) {
            request.setAttribute(ContextKeys.CLIENT_IP, clientIp);
            log.trace("获取客户端 IP 地址完成：{}", clientIp);
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}