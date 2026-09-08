package com.weutil.common.startup;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 服务器 IP 地址初始化器
 *
 * <h2>功能说明
 * <p>在应用启动后自动执行，获取服务器的公网 IP 和内网 IP 并缓存，供其他组件通过依赖注入使用。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Component
public class ServerIpInitializer implements ApplicationRunner {

    /** 内网 IP 占位符 */
    public static final String PLACEHOLDER_PRIVATE_IP = "PRIVATE_IP";

    /** 公网 IP 占位符 */
    public static final String PLACEHOLDER_PUBLIC_IP = "PUBLIC_IP";

    /** 公网 IP */
    @Getter
    private String publicIp;

    /** 内网 IP */
    @Getter
    private String internalIp;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        internalIp = fetchInternalIp();
        publicIp = fetchPublicIp();

        log.info("服务器 IP 地址检测完成，公网 IP: {}，内网 IP: {}", publicIp, internalIp);
    }

    /**
     * 解析字符串中的 IP 占位符
     *
     * <h3>处理逻辑
     * <p>检测输入字符串中的 PRIVATE_IP/PUBLIC_IP 占位符，并替换为运行时获取的真实 IP。
     * <p>占位符可以出现在字符串的任何位置，不限于 URL 的 host 部分。
     *
     * @param input 包含占位符的输入字符串
     * @return 替换占位符后的字符串
     */
    public String resolveIpPlaceholder(String input) {
        if (input == null) {
            return null;
        }

        // 替换 PRIVATE_IP 和 PUBLIC_IP 占位符
        return input
            .replace(PLACEHOLDER_PRIVATE_IP, internalIp)
            .replace(PLACEHOLDER_PUBLIC_IP, publicIp);
    }

    /**
     * 获取服务器内网 IP 地址
     *
     * <h3>处理逻辑
     * <p>遍历所有网络接口，筛选有效网卡并获取 IPv4 私有地址（10.x、192.168.x、172.16-31.x）。
     * <p>过滤掉未启用、回环、虚拟网卡。
     *
     * @return 内网 IP 地址，未找到时返回 "unknown"
     */
    private String fetchInternalIp() {
        // NetworkInterface.getNetworkInterfaces() 声明了受检异常，且在无网卡环境下可能失败，
        // 需要捕获异常并降级返回 "unknown" 保证启动流程不中断
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress();

                        if (isPrivateIp(ip)) {
                            return ip;
                        }
                    }
                }
            }

            log.warn("未找到内网 IP 地址，将使用 unknown 作为默认值");
            return "unknown";
        } catch (Exception e) {
            log.warn("获取内网 IP 地址失败，将使用 unknown 作为默认值", e);
            return "unknown";
        }
    }

    /**
     * 判断 IPv4 地址是否为私有地址
     *
     * <h3>判断规则
     * <p>RFC 1918 定义的私有网段：10.0.0.0/8、172.16.0.0/12（即 172.16-31.x.x）、192.168.0.0/16。
     * <p>172 段仅 16-31 为私有地址，172.0-15.x.x 与 172.32+ 均为公网地址。
     *
     * @param ip IPv4 地址字符串
     * @return 是私有地址返回 true
     */
    private boolean isPrivateIp(String ip) {
        if (ip.startsWith("10.") || ip.startsWith("192.168.")) {
            return true;
        }

        if (ip.startsWith("172.")) {
            int secondSegment = Integer.parseInt(ip.split("\\.")[1]);
            return secondSegment >= 16 && secondSegment <= 31;
        }

        return false;
    }

    /**
     * 获取服务器公网 IP 地址
     *
     * <h3>处理逻辑
     * <p>通过请求 ifconfig.me/ip 接口获取公网 IP，响应体即为 IP 字符串。
     *
     * @return 公网 IP 地址，请求失败时返回 "unknown"
     */
    private String fetchPublicIp() {
        // 外部网络请求在内网隔离或网络异常环境下可能失败，
        // 捕获异常并降级返回 "unknown" 保证启动流程不中断
        try {
            return RestClient.create()
                .get()
                .uri("https://ifconfig.me/ip")
                .retrieve()
                .body(String.class);
        } catch (Exception e) {
            log.warn("获取公网 IP 失败，将使用 unknown 作为默认值", e);
            return "unknown";
        }
    }
}
