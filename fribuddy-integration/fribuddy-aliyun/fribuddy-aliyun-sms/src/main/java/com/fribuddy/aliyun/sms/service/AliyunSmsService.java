package com.fribuddy.aliyun.sms.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.tea.TeaException;
import com.fribuddy.aliyun.sms.config.AliyunSmsProperties;
import com.fribuddy.common.exception.ThirdPartySdkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里云短信服务
 *
 * <h2>说明
 * <p>封装阿里云短信服务的短信发送能力，当前提供验证码短信发送。
 * <p>手机号属于个人敏感信息，日志和异常消息中一律使用脱敏后的手机号。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunSmsService {

    /** 短信服务客户端 */
    private final Client smsClient;

    /** 阿里云短信服务配置属性 */
    private final AliyunSmsProperties aliyunSmsProperties;

    // ================================ public 方法 ================================

    /**
     * 发送验证码短信
     *
     * <h3>发送说明
     * <p>使用验证码专用模板发送短信，模板变量为 code，内容为验证码本身。
     * <p>接口调用异常与业务失败均转换为第三方 SDK 异常，由全局异常处理器统一处理。
     *
     * @param phoneNumber 接收短信的手机号
     * @param code        验证码
     */
    public void sendVerificationCode(String phoneNumber, String code) {
        // 构建短信发送请求，将验证码填入模板变量 code
        SendSmsRequest request = new SendSmsRequest()
            .setPhoneNumbers(phoneNumber)
            .setSignName(aliyunSmsProperties.getSignName())
            .setTemplateCode(aliyunSmsProperties.getTemplateCode())
            .setTemplateParam(String.format("{\"code\":\"%s\"}", code));

        // 调用短信发送接口
        SendSmsResponse response = doSendSms(request);

        // 响应体业务码为 OK 才表示发送成功，业务失败转换为第三方 SDK 异常
        SendSmsResponseBody body = response.getBody();
        if (body == null || !"OK".equals(body.getCode())) {
            throw new ThirdPartySdkException(
                String.format(
                    "发送阿里云验证码短信失败，手机号：%s，业务码：%s",
                    maskPhone(phoneNumber),
                    body == null ? null : body.getCode()
                )
            );
        }

        log.info("阿里云验证码短信发送成功，手机号：{}，发送流水号：{}", maskPhone(phoneNumber), body.getBizId());
    }

    // ================================ private 方法 ================================

    /**
     * 调用短信发送接口
     *
     * <h3>异常转换
     * <p>SDK 方法声明抛出受检异常，在此捕获并统一转换为第三方 SDK 异常。
     * <p>TeaException 携带阿里云返回的诊断地址，一并写入异常消息。
     *
     * @param request 短信发送请求
     * @return 短信发送响应
     */
    private SendSmsResponse doSendSms(SendSmsRequest request) {
        // SDK 方法声明抛出受检 Exception，必须在此捕获并转换为业务异常
        try {
            return smsClient.sendSms(request);
        } catch (TeaException e) {
            throw new ThirdPartySdkException(
                String.format(
                    "调用阿里云短信接口失败，手机号：%s，错误信息：%s，诊断地址：%s",
                    maskPhone(request.getPhoneNumbers()),
                    e.getMessage(),
                    e.getData() == null ? null : e.getData().get("Recommend")
                ),
                e
            );
        } catch (Exception e) {
            throw new ThirdPartySdkException(
                String.format("调用阿里云短信接口失败，手机号：%s", maskPhone(request.getPhoneNumbers())),
                e
            );
        }
    }

    /**
     * 脱敏手机号
     *
     * <h3>脱敏规则
     * <p>长度充足时保留前 3 位和后 4 位，中间替换为 4 个星号（如 138****1234）。
     * <p>长度不足 8 位时仅保留后 4 位，前缀替换为 4 个星号。
     *
     * @param phoneNumber 原始手机号
     * @return 脱敏后的手机号
     */
    private String maskPhone(String phoneNumber) {
        if (phoneNumber.length() < 8) {
            return "****" + phoneNumber.substring(Math.max(0, phoneNumber.length() - 4));
        }

        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
