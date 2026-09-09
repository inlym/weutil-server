package com.weutil.common.filter;

import com.weutil.common.constants.ContextKeys;
import com.weutil.common.constants.CustomHttpHeader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 分布式链路追踪过滤器
 *
 * <h2>说明
 * <p>用于生成唯一的 trace ID，支持分布式系统中的请求链路追踪。
 * <p>为每个 HTTP 请求分配唯一标识符，用于日志关联、问题排查和性能监控。
 *
 * <h2>功能特性
 * <ul>
 *   <li>为每个请求生成唯一的 UUID 作为 trace ID</li>
 *   <li>将 trace ID 存储到 MDC 中，支持日志输出</li>
 *   <li>在响应头中返回 x-trace-id，便于客户端关联</li>
 *   <li>确保在过滤器链中最早执行，保证全链路追踪</li>
 * </ul>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Component
public class TraceIdFilter extends OncePerRequestFilter implements Ordered {

    /** Ant 路径匹配器 */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 获取过滤器执行顺序
     *
     * <h3>执行顺序说明
     * <p>返回最高优先级，确保该过滤器在过滤器链中最先执行
     * <p>保证所有后续处理都能获取到 trace ID，实现完整链路追踪
     *
     * @return 过滤器执行顺序优先级值
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 判断是否需要跳过过滤处理
     *
     * <h3>跳过条件
     * <p>WebSocket 握手路径跳过过滤处理，避免在该路径设置不必要的 trace ID
     *
     * @param request HTTP 请求对象
     * @return true 表示跳过过滤处理，false 表示执行过滤处理
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return PATH_MATCHER.match("/ws/**", path);
    }

    /**
     * 执行过滤器内部逻辑
     *
     * <h3>处理流程
     * <p>1. 生成唯一的 trace ID
     * <p>2. 将 trace ID 存储到 MDC 上下文中
     * <p>3. 在响应头中设置 trace ID
     * <p>4. 继续执行过滤器链
     * <p>5. 清理 MDC 上下文，防止内存泄漏
     *
     * <h3>关键机制
     * <p>使用 try-finally 确保 MDC 上下文在请求处理后得到清理，防止内存泄漏和上下文污染
     * <p>不使用 try-finally 会导致线程池中线程的 MDC 传递给下一个请求，造成数据混乱
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
        // 生成请求唯一的链路追踪 ID
        String traceId = UUID.randomUUID().toString();

        try {
            // 将 trace ID 写入日志上下文，使本次请求的所有日志均携带该标识
            MDC.put(ContextKeys.TRACE_ID, traceId);

            // 响应头回传 trace ID，便于客户端反馈问题时关联服务端日志
            response.setHeader(CustomHttpHeader.TRACE_ID, traceId);

            log.trace("收到请求 {} {}", request.getMethod(), request.getRequestURI());

            // 继续执行过滤器链
            filterChain.doFilter(request, response);
        } finally {
            // 清理 MDC 上下文，防止线程池线程复用时 trace ID 泄漏到下一个请求
            MDC.clear();
        }
    }
}
