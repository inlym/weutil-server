package com.weutil.aliyun.captcha.service;

import com.aliyun.captcha20230305.Client;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponseBody;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponseBody.VerifyIntelligentCaptchaResponseBodyResult;
import com.aliyun.tea.TeaException;
import com.weutil.aliyun.captcha.config.AliyunCaptchaProperties;
import com.weutil.common.exception.ThirdPartySdkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 阿里云验证码服务
 *
 * <h2>说明
 * <p>封装阿里云验证码 2.0 的服务端验签能力，对客户端回调产生的验证码参数发起校验。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunCaptchaService {

    /** 验证码服务客户端 */
    private final Client captchaClient;

    /** 阿里云验证码服务配置属性 */
    private final AliyunCaptchaProperties aliyunCaptchaProperties;

    // ================================ public 方法 ================================

    /**
     * 校验验证码参数
     *
     * <h3>校验说明
     * <p>调用验证码 2.0 的智能验证接口，验证码参数由客户端验证码脚本回调产生，必须原样透传，禁止修改。
     * <p>接口调用失败抛出第三方 SDK 异常；验证不通过属于正常业务结果，返回 false。
     *
     * @param captchaVerifyParam 客户端回调产生的验证码参数
     * @return 验证结果，true 表示验证通过，false 表示验证不通过
     */
    public boolean verify(String captchaVerifyParam) {
        // 验证码参数为空时无需发起远端验签，直接按验证不通过处理
        if (!StringUtils.hasText(captchaVerifyParam)) {
            log.trace("验证码参数为空，跳过验签，直接按不通过处理");
            return false;
        }

        // 构建验签请求，场景 ID 由服务端写入，防止前端被篡改为其他场景
        VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest()
            .setCaptchaVerifyParam(captchaVerifyParam)
            .setSceneId(aliyunCaptchaProperties.getSceneId());

        // 调用智能验证接口完成验签
        VerifyIntelligentCaptchaResponseBody body = doVerify(request).getBody();

        // 响应未标记成功说明接口调用失败，转换为第三方 SDK 异常
        if (body == null || !Boolean.TRUE.equals(body.getSuccess())) {
            throw new ThirdPartySdkException(
                String.format(
                    "调用阿里云验证码验签接口失败，业务码：%s，错误信息：%s",
                    body == null ? null : body.getCode(),
                    body == null ? null : body.getMessage()
                )
            );
        }

        // 取出验签结果，结果模型缺失或未标记通过均按验证不通过处理
        VerifyIntelligentCaptchaResponseBodyResult result = body.getResult();
        boolean passed = result != null && Boolean.TRUE.equals(result.getVerifyResult());

        log.info("阿里云验证码验签完成，验证结果：{}，校验码：{}", passed, result == null ? null : result.getVerifyCode());

        return passed;
    }

    // ================================ private 方法 ================================

    /**
     * 调用智能验证接口
     *
     * <h3>异常转换
     * <p>SDK 方法声明抛出受检异常，在此捕获并统一转换为第三方 SDK 异常。
     * <p>TeaException 携带阿里云返回的诊断地址，一并写入异常消息。
     *
     * @param request 智能验证请求
     * @return 智能验证响应
     */
    private VerifyIntelligentCaptchaResponse doVerify(VerifyIntelligentCaptchaRequest request) {
        // SDK 方法声明抛出受检 Exception，必须在此捕获并转换为业务异常
        try {
            return captchaClient.verifyIntelligentCaptcha(request);
        } catch (TeaException e) {
            throw new ThirdPartySdkException(
                String.format(
                    "调用阿里云验证码验签接口异常，错误信息：%s，诊断地址：%s",
                    e.getMessage(),
                    e.getData() == null ? null : e.getData().get("Recommend")
                ),
                e
            );
        } catch (Exception e) {
            throw new ThirdPartySdkException("调用阿里云验证码验签接口异常", e);
        }
    }
}
