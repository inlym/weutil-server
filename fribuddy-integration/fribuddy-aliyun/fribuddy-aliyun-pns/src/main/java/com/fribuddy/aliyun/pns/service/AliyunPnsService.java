package com.fribuddy.aliyun.pns.service;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponseBody;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponseBody.SendSmsVerifyCodeResponseBodyModel;
import com.aliyun.tea.TeaException;
import com.fribuddy.aliyun.pns.config.AliyunPnsProperties;
import com.fribuddy.aliyun.pns.exception.MobileNumberIllegalException;
import com.fribuddy.aliyun.pns.exception.SmsFrequencyLimitException;
import com.fribuddy.aliyun.pns.exception.SmsSendFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里云号码认证服务
 *
 * <h2>说明
 * <p>封装阿里云号码认证服务的能力，当前提供短信认证验证码发送。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunPnsService {

    /** 短信认证模板编码 */
    private static final String TEMPLATE_CODE = "100001";

    /** 短信认证模板变量，code 为服务端验证码占位符，min 为短信文案中展示的有效分钟数 */
    private static final String TEMPLATE_PARAM = "{\"code\":\"##code##\",\"min\":\"5\"}";

    /** 验证码长度 */
    private static final Long CODE_LENGTH = 6L;

    /** 验证码有效期（秒），须与模板变量中展示的有效分钟数保持一致 */
    private static final Long VALID_TIME = 300L;

    /** 验证码类型，1 表示纯数字 */
    private static final Long CODE_TYPE = 1L;

    /** 响应中返回验证码，由调用方保存后自行校验 */
    private static final Boolean RETURN_VERIFY_CODE = true;

    /** 发送成功的响应业务码 */
    private static final String SUCCESS_CODE = "OK";

    /** 手机号格式异常的响应业务码 */
    private static final String MOBILE_NUMBER_ILLEGAL_CODE = "isv.MOBILE_NUMBER_ILLEGAL";

    /** 发送频次超出限制的响应业务码 */
    private static final String FREQUENCY_LIMIT_CODE = "biz.FREQUENCY";

    /** 号码认证服务客户端 */
    private final Client pnsClient;

    /** 阿里云号码认证服务配置属性 */
    private final AliyunPnsProperties aliyunPnsProperties;

    // ================================ public 方法 ================================

    /**
     * 发送短信认证验证码
     *
     * <h3>发送说明
     * <p>调用号码认证服务的短信认证接口，由阿里云生成 6 位纯数字验证码并下发短信，有效期 5 分钟。
     * <p>发送失败按响应业务码区分为手机号格式异常、发送频次超限异常和发送失败异常，由异常处理器统一处理。
     *
     * @param phoneNumber 接收短信的手机号
     * @return 服务端生成的验证码，由调用方保存并校验
     */
    public String sendSmsVerifyCode(String phoneNumber) {
        // 构建短信认证请求，除手机号外的请求参数均为固定项
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
            .setPhoneNumber(phoneNumber)
            .setSignName(aliyunPnsProperties.getSignName())
            .setTemplateCode(TEMPLATE_CODE)
            .setTemplateParam(TEMPLATE_PARAM)
            .setCodeLength(CODE_LENGTH)
            .setValidTime(VALID_TIME)
            .setCodeType(CODE_TYPE)
            .setReturnVerifyCode(RETURN_VERIFY_CODE);

        // 调用短信认证发送接口
        SendSmsVerifyCodeResponse response = doSendSmsVerifyCode(request);

        // 响应体业务码为 OK 才表示发送成功，非 OK 业务码需区分异常类型
        SendSmsVerifyCodeResponseBody body = response.getBody();
        String code = body == null ? null : body.getCode();

        // 手机号格式不合法，抛出手机号格式异常
        if (MOBILE_NUMBER_ILLEGAL_CODE.equals(code)) {
            throw new MobileNumberIllegalException(
                String.format("阿里云短信认证返回手机号格式异常，手机号：%s", phoneNumber)
            );
        }

        // 发送频次超出阿里云限制，抛出频次超限异常
        if (FREQUENCY_LIMIT_CODE.equals(code)) {
            throw new SmsFrequencyLimitException(
                String.format("阿里云短信认证返回发送频次超出限制，手机号：%s", phoneNumber)
            );
        }

        // 其余业务码统一按发送失败处理
        if (!SUCCESS_CODE.equals(code)) {
            throw new SmsSendFailureException(
                String.format("发送阿里云短信认证验证码失败，手机号：%s，业务码：%s", phoneNumber, code)
            );
        }

        // 取出响应模型中的验证码，未随响应返回时视为发送失败
        SendSmsVerifyCodeResponseBodyModel model = body.getModel();
        if (model == null || model.getVerifyCode() == null) {
            throw new SmsSendFailureException(
                String.format("阿里云短信认证响应中缺少验证码，手机号：%s", phoneNumber)
            );
        }

        log.info("阿里云短信认证验证码发送成功，手机号：{}，验证码：{}，发送流水号：{}", phoneNumber, model.getVerifyCode(), model.getBizId());

        return model.getVerifyCode();
    }

    // ================================ private 方法 ================================

    /**
     * 调用短信认证发送接口
     *
     * <h3>异常转换
     * <p>SDK 方法声明抛出受检异常，在此捕获并统一转换为短信发送失败异常。
     * <p>TeaException 携带阿里云返回的诊断地址，一并写入异常消息。
     *
     * @param request 短信认证发送请求
     * @return 短信认证发送响应
     */
    private SendSmsVerifyCodeResponse doSendSmsVerifyCode(SendSmsVerifyCodeRequest request) {
        // SDK 方法声明抛出受检 Exception，必须在此捕获并转换为业务异常
        try {
            return pnsClient.sendSmsVerifyCode(request);
        } catch (TeaException e) {
            throw new SmsSendFailureException(
                String.format(
                    "调用阿里云短信认证接口失败，手机号：%s，错误信息：%s，诊断地址：%s",
                    request.getPhoneNumber(),
                    e.getMessage(),
                    e.getData() == null ? null : e.getData().get("Recommend")
                ),
                e
            );
        } catch (Exception e) {
            throw new SmsSendFailureException(
                String.format("调用阿里云短信认证接口失败，手机号：%s", request.getPhoneNumber()),
                e
            );
        }
    }
}
