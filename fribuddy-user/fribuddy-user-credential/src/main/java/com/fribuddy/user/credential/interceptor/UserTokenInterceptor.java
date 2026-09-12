package com.fribuddy.user.credential.interceptor;

import com.fribuddy.user.credential.entity.UserCredential;
import com.fribuddy.user.credential.service.UserCredentialService;
import com.fribuddy.common.annotation.UserPermission;
import com.fribuddy.common.constants.ContextKeys;
import com.fribuddy.common.constants.CustomHttpHeader;
import com.fribuddy.common.model.auth.SimpleUserAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

/**
 * 用户令牌拦截器
 *
 * <h2>说明
 * <p>替代原 UserCredentialFilter，通过检查控制器方法是否有 @UserPermission 注解来判断是否需要处理。
 * <p>在拦截器中可以访问 HandlerMethod，从而支持注解检查，这是过滤器无法做到的。
 *
 * <h2>处理流程
 * <ul>
 *   <li>检查 handler 是否为 HandlerMethod，不是则直接放行</li>
 *   <li>检查方法是否有 @UserPermission 注解，没有则直接放行</li>
 *   <li>从请求头获取用户令牌并验证，有效则设置 SecurityContext 和请求属性</li>
 *   <li>令牌无效时不设置认证上下文，由 @Secured 注解拒绝访问，
 *       全局异常处理器转换为 401 未登录响应</li>
 * </ul>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@RequiredArgsConstructor
public class UserTokenInterceptor implements HandlerInterceptor {

    /** 续期触发阈值，凭证剩余有效期低于该时长时执行续期 */
    private static final Duration RENEWAL_THRESHOLD = Duration.ofDays(10);

    /** 用户认证凭证服务 */
    private final UserCredentialService userCredentialService;

    // ================================ public 方法 ================================

    /**
     * 请求前置处理
     *
     * <h3>处理逻辑
     * <p>判断当前请求的目标方法是否标注了 @UserPermission 注解，若有则验证用户令牌。
     * <p>始终返回 true 放行，权限校验交由 @Secured 注解处理。
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  处理器对象
     * @return 始终返回 true
     */
    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) {
        // 非 HandlerMethod（如静态资源）无需令牌验证
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 未标注 @UserPermission 的方法无需令牌验证
        if (!handlerMethod.hasMethodAnnotation(UserPermission.class)) {
            return true;
        }

        // 从请求头获取用户令牌，未携带或为空白则放行，后续由权限注解拦截
        String token = request.getHeader(CustomHttpHeader.USER_TOKEN);
        if (!StringUtils.hasText(token)) {
            log.warn("访问用户接口未携带令牌，路径：{}", request.getServletPath());
            return true;
        }

        // 令牌为敏感凭据，禁止写入日志
        // 验证令牌有效性，无效令牌不设置认证上下文
        UserCredential credential = userCredentialService.findValidByToken(token);
        if (credential == null) {
            log.trace("用户认证失败，令牌无效或已过期");
            return true;
        }

        // 构建用户认证对象并注入 SecurityContext
        SimpleUserAuthentication authentication = new SimpleUserAuthentication(credential.getUserId());
        SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();
        SecurityContext context = strategy.createEmptyContext();
        context.setAuthentication(authentication);
        strategy.setContext(context);

        // 将用户 ID 写入请求属性，供 @UserId 参数解析器使用
        request.setAttribute(ContextKeys.USER_ID, credential.getUserId());

        // 凭证剩余有效期低于续期阈值，执行续期
        if (credential.getExpireTime().isBefore(Instant.now().plus(RENEWAL_THRESHOLD))) {
            userCredentialService.renew(credential);
        }

        return true;
    }
}
