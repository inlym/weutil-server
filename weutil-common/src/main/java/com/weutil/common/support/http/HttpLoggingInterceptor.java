package com.weutil.common.support.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/**
 * HTTP 请求日志拦截器
 *
 * <h2>拦截器说明
 * <p>拦截所有通过 RestClient 发出的 HTTP 请求，记录请求和响应的详细信息。
 * <p>日志包含请求耗时、HTTP 方法、URL、状态码、请求体和响应体等信息。
 * <p>对于超过 500 字符的响应，自动截断为前 250 字符和后 250 字符。
 * <p>对于超过 1MB 的响应体，仅记录大小而不记录内容。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 **/
@Slf4j
public class HttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    /** 响应体大小限制（1MB），超过此限制仅记录大小 */
    private static final int MAX_RESPONSE_BODY_SIZE = 1024 * 1024;

    /**
     * 拦截 HTTP 请求并记录日志
     *
     * @param request   HTTP 请求
     * @param body      请求体字节数组
     * @param execution 请求执行器
     * @return HTTP 响应，不为 null
     */
    @Override
    public ClientHttpResponse intercept(
        HttpRequest request,
        byte[] body,
        ClientHttpRequestExecution execution
    ) throws IOException {
        // 记录请求开始时间，用于计算耗时
        Instant startTime = Instant.now();

        // 记录请求开始日志
        String requestData = body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "[Empty]";
        log.trace("[HTTP] [Start] {} {}\n{}", request.getMethod(), request.getURI(), requestData);

        // 执行实际 HTTP 请求
        ClientHttpResponse response = execution.execute(request, body);

        // 记录响应日志及耗时
        long duration = Duration.between(startTime, Instant.now()).toMillis();
        byte[] responseBodyBytes = StreamUtils.copyToByteArray(response.getBody());
        String formattedResponseData = formatResponseData(responseBodyBytes);
        log.trace("[HTTP] [End] {}ms {}\n{}", duration, response.getStatusCode(), formattedResponseData);

        return response;
    }

    /**
     * 将响应体格式化为可打印字符串
     *
     * <h3>截断规则
     * <p>超过 500 字符时保留前 250 字符和后 250 字符，中间用省略号连接。
     * <p>超过 1MB 时仅记录大小，不记录内容。
     *
     * @param responseBodyBytes 响应体字节数组
     * @return 格式化后的响应字符串，不为 null
     */
    private String formatResponseData(byte[] responseBodyBytes) {
        if (responseBodyBytes == null || responseBodyBytes.length == 0) {
            return "[Empty]";
        }

        // 超过 1MB 仅记录大小
        if (responseBodyBytes.length > MAX_RESPONSE_BODY_SIZE) {
            return "[Response too large: " + responseBodyBytes.length / 1024 + "KB]";
        }

        String responseData = new String(responseBodyBytes, StandardCharsets.UTF_8);

        // 响应体较短，无需截断，直接返回
        if (responseData.length() <= 500) {
            return responseData;
        }

        // 响应体过长，截断为首尾各 250 字符，避免日志过大
        String prefix = responseData.substring(0, 250);
        String suffix = responseData.substring(responseData.length() - 250);
        return prefix + " ... " + suffix;
    }
}