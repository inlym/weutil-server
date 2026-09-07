package com.weutil.aliyun.oss.service;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.exceptions.OperationException;
import com.aliyun.sdk.service.oss2.exceptions.ServiceException;
import com.aliyun.sdk.service.oss2.models.CopyObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.weutil.aliyun.oss.config.AliyunOssProperties;
import com.weutil.common.exception.ExternalApiException;
import com.weutil.common.exception.ThirdPartySdkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 阿里云 OSS 服务
 *
 * <h2>说明
 * <p>封装阿里云 OSS 的对象操作能力，面向用户文件桶，当前提供从 URL 转存资源和桶内转储资源。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunOssService {

    /** OSS 客户端 */
    private final OSSClient ossClient;

    /** 阿里云 OSS 配置属性 */
    private final AliyunOssProperties aliyunOssProperties;

    /** REST 客户端，用于下载外部资源 */
    private final RestClient restClient;

    // ================================ public 方法 ================================

    /**
     * 从 URL 转存资源
     *
     * <h3>处理流程
     * <p>先用 REST 客户端下载 URL 指向的资源内容，再上传到用户文件桶的指定 key 上。
     * <p>下载失败转换为外部 API 异常，上传失败转换为第三方 SDK 异常，由全局异常处理器统一处理。
     *
     * @param url 资源的 URL 地址
     * @param key 保存到桶上的对象 key
     */
    public void saveFromUrl(String url, String key) {
        // 下载 URL 指向的资源内容
        byte[] content = doDownload(url);

        // 上传到用户文件桶的指定 key
        doPutObject(key, content);

        log.info("资源转存成功，资源地址：{}，键名：{}，大小：{} 字节", url, key, content.length);
    }

    /**
     * 转储资源
     *
     * <h3>处理流程
     * <p>在用户文件桶内以服务端复制方式将原始 key 的对象复制到目标 key，流量不经服务端中转。
     * <p>原始 key 不存在时由 OSS 返回错误，转换为第三方 SDK 异常。
     *
     * @param sourceKey 原始 key
     * @param targetKey 目标 key
     */
    public void transfer(String sourceKey, String targetKey) {
        // 桶内服务端复制，将原始 key 的对象复制到目标 key
        doCopyObject(sourceKey, targetKey);

        log.info("资源转储成功，原始键名：{}，目标键名：{}", sourceKey, targetKey);
    }

    // ================================ private 方法 ================================

    /**
     * 下载资源内容
     *
     * <h3>异常转换
     * <p>REST 客户端抛出的异常为非受检异常，在此捕获并统一转换为外部 API 异常。
     *
     * @param url 资源的 URL 地址
     * @return 资源内容的字节数组
     */
    private byte[] doDownload(String url) {
        // REST 客户端抛出的异常为非受检异常，必须在此捕获并转换为业务异常
        try {
            byte[] content = restClient.get().uri(url).retrieve().body(byte[].class);

            // 下载内容为空说明资源无效，无可转存内容
            if (content == null || content.length == 0) {
                throw new ExternalApiException(String.format("下载资源内容为空，资源地址：%s", url));
            }

            return content;
        } catch (RestClientException e) {
            throw new ExternalApiException(String.format("下载资源失败，资源地址：%s", url), e);
        }
    }

    /**
     * 上传对象
     *
     * <h3>异常转换
     * <p>SDK 抛出的异常为非受检异常，在此捕获并统一转换为第三方 SDK 异常。
     *
     * @param key     对象 key
     * @param content 对象内容的字节数组
     */
    private void doPutObject(String key, byte[] content) {
        PutObjectRequest request = PutObjectRequest
            .newBuilder()
            .bucket(aliyunOssProperties.getBucketName())
            .key(key)
            .body(BinaryData.fromBytes(content))
            .build();

        // SDK 抛出的异常为非受检异常，必须在此捕获并转换为业务异常
        try {
            ossClient.putObject(request);
        } catch (ServiceException e) {
            throw new ThirdPartySdkException(
                String.format(
                    "上传对象到 OSS 失败，桶：%s，键名：%s，业务码：%s，请求 ID：%s",
                    aliyunOssProperties.getBucketName(),
                    key,
                    e.errorCode(),
                    e.requestId()
                ),
                e
            );
        } catch (OperationException e) {
            throw new ThirdPartySdkException(
                String.format("上传对象到 OSS 失败，桶：%s，键名：%s", aliyunOssProperties.getBucketName(), key),
                e
            );
        }
    }

    /**
     * 桶内复制对象
     *
     * <h3>异常转换
     * <p>SDK 抛出的异常为非受检异常，在此捕获并统一转换为第三方 SDK 异常。
     *
     * @param sourceKey 原始 key
     * @param targetKey 目标 key
     */
    private void doCopyObject(String sourceKey, String targetKey) {
        CopyObjectRequest request = CopyObjectRequest
            .newBuilder()
            .bucket(aliyunOssProperties.getBucketName())
            .key(targetKey)
            .sourceBucket(aliyunOssProperties.getBucketName())
            .sourceKey(sourceKey)
            .build();

        // SDK 抛出的异常为非受检异常，必须在此捕获并转换为业务异常
        try {
            ossClient.copyObject(request);
        } catch (ServiceException e) {
            throw new ThirdPartySdkException(
                String.format(
                    "转储 OSS 对象失败，桶：%s，原始键名：%s，目标键名：%s，业务码：%s，请求 ID：%s",
                    aliyunOssProperties.getBucketName(),
                    sourceKey,
                    targetKey,
                    e.errorCode(),
                    e.requestId()
                ),
                e
            );
        } catch (OperationException e) {
            throw new ThirdPartySdkException(
                String.format(
                    "转储 OSS 对象失败，桶：%s，原始键名：%s，目标键名：%s",
                    aliyunOssProperties.getBucketName(),
                    sourceKey,
                    targetKey
                ),
                e
            );
        }
    }
}
