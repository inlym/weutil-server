package com.weutil.common.config;

import com.weutil.common.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Jackson 全局配置测试
 *
 * <h2>说明
 * <p>逐项验证 JacksonConfig 注入的序列化与反序列化策略是否真实生效，覆盖未知字段忽略、未知枚举容错、空 Bean 序列化、UTC 时区、Instant 毫秒时间戳双向转换与 null 字段过滤。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@JsonTest
@ContextConfiguration(classes = JacksonConfig.class)
class JacksonConfigTest {

    /** 已应用 JacksonConfig 全部策略的 JSON 序列化映射器，由 Spring 注入 */
    @Autowired
    private JsonMapper jsonMapper;

    // ================================ 测试方法 ================================

    /**
     * 未知字段被忽略
     *
     * <h3>验证配置
     * <p>DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES 已禁用，JSON 中的未知字段不触发反序列化异常。
     */
    @Test
    void unknownFieldsAreIgnored() {
        SampleDTO dto = jsonMapper.readValue(
            "{\"username\":\"alice\",\"extra\":\"ignored\"}",
            SampleDTO.class
        );

        assertThat(dto.getUsername()).isEqualTo("alice");
    }

    /**
     * 未知枚举值反序列化为 null
     *
     * <h3>验证配置
     * <p>EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL 已启用，无法识别的枚举字符串映射为 null 而非抛出异常。
     */
    @Test
    void unknownEnumValueBecomesNull() {
        EnumHolder holder = jsonMapper.readValue("{\"status\":\"UNKNOWN\"}", EnumHolder.class);

        assertThat(holder.getStatus()).isNull();
    }

    /**
     * 空对象序列化不抛异常
     *
     * <h3>验证配置
     * <p>SerializationFeature.FAIL_ON_EMPTY_BEANS 已禁用，没有任何字段的对象序列化为 {} 而非报错。
     */
    @Test
    void emptyBeanSerializedWithoutError() {
        String json = jsonMapper.writeValueAsString(new EmptyBean());

        assertThat(json).isEqualTo("{}");
    }

    /**
     * 默认时区为 UTC
     *
     * <h3>验证配置
     * <p>defaultTimeZone(UTC) 已生效。当前项目时间类型统一序列化为时间戳，UTC 时区对输出无可观察行为，因此直接断言映射器的时区设置。
     */
    @Test
    void defaultTimeZoneIsUtc() {
        TimeZone timeZone = jsonMapper.serializationConfig().getTimeZone();

        assertThat(timeZone).isEqualTo(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Instant 序列化为毫秒时间戳
     *
     * <h3>验证配置
     * <p>DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS 已开启（输出数字而非 ISO-8601 字符串），且 WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS 已关闭（毫秒精度而非纳秒），两项配置共同使 Instant 序列化为毫秒数字。
     */
    @Test
    void instantSerializedAsMillisTimestamp() {
        Instant fixed = Instant.ofEpochMilli(1700000000000L);
        SampleDTO dto = SampleDTO.builder().username("alice").createTime(fixed).build();

        String json = jsonMapper.writeValueAsString(dto);

        assertThat(json).contains("\"createTime\":1700000000000");
    }

    /**
     * 毫秒时间戳反序列化为 Instant
     *
     * <h3>验证配置
     * <p>DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS 已关闭，数字时间戳按毫秒精度解析为 Instant。
     */
    @Test
    void millisTimestampDeserializedToInstant() {
        SampleDTO dto = jsonMapper.readValue("{\"createTime\":1700000000000}", SampleDTO.class);

        assertThat(dto.getCreateTime()).isEqualTo(Instant.ofEpochMilli(1700000000000L));
    }

    /**
     * null 字段被过滤
     *
     * <h3>验证配置
     * <p>JsonInclude.Include.NON_NULL 已启用，值为 null 的字段不出现在序列化输出中。
     */
    @Test
    void nullFieldsExcluded() {
        SampleDTO dto = SampleDTO.builder().username("alice").build();

        String json = jsonMapper.writeValueAsString(dto);

        assertThat(json).contains("username");
        assertThat(json).doesNotContain("age", "createTime", "remark");
    }

    /**
     * 静态工具与 Spring 映射器行为一致
     *
     * <h3>验证配置
     * <p>JsonUtils 静态实例与 Spring 管理的 JsonMapper 共用 customizeBuilder 同一份配置，
     * <p>静态场景（WebSocket 消息、Schema 生成）与 HTTP 层的序列化结果保持一致。
     */
    @Test
    void staticUtilsMatchesSpringManagedMapper() {
        Instant fixed = Instant.ofEpochMilli(1700000000000L);
        SampleDTO dto = SampleDTO.builder().username("alice").createTime(fixed).build();

        assertThat(JsonUtils.stringify(dto)).isEqualTo(jsonMapper.writeValueAsString(dto));
        assertThat(JsonUtils.parse("{\"createTime\":1700000000000}", SampleDTO.class).getCreateTime())
            .isEqualTo(Instant.ofEpochMilli(1700000000000L));
    }

    // ================================ 测试专用类型 ================================

    /** 测试用示例 DTO，包含常用字段类型以验证序列化与反序列化配置 */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SampleDTO {

        /** 用户名 */
        private String username;

        /** 年龄 */
        private Integer age;

        /** 创建时间 */
        private Instant createTime;

        /** 备注 */
        private String remark;
    }

    /** 测试状态枚举，用于验证未知枚举值容错 */
    private enum Status {
        ACTIVE,
        INACTIVE
    }

    /** 含枚举字段的测试载体，用于验证枚举反序列化 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class EnumHolder {
        /** 状态枚举字段 */
        private Status status;
    }

    /** 无字段的空对象，用于验证空 Bean 序列化容错 */
    private static class EmptyBean {
    }
}
