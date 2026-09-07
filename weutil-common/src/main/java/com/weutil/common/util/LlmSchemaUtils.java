package com.weutil.common.util;

import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import com.github.victools.jsonschema.module.swagger2.Swagger2Module;
import org.springframework.ai.openai.OpenAiChatModel;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;

import java.util.Map;

/**
 * 大模型 JSON Schema 响应格式工具类
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class LlmSchemaUtils {

    // 私有构造函数，防止实例化
    private LlmSchemaUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 构建 JSON Schema 响应格式（OpenAI）
     *
     * <h3>说明
     * <p>Spring AI 2.0 中 ResponseFormat.jsonSchema 接受纯 JSON Schema 字符串，
     * <p>name 和 strict 由 OpenAiChatModel 内部硬编码（"json_schema" 和 true），无需在此指定。
     *
     * @param targetClass 响应结果类
     * @return OpenAI JSON Schema 响应格式
     */
    public static OpenAiChatModel.ResponseFormat buildOpenAiJsonSchemaFormat(Class<?> targetClass) {
        Map<String, Object> schemaMap = buildSchemaMap(targetClass);

        return OpenAiChatModel.ResponseFormat
            .builder()
            .type(OpenAiChatModel.ResponseFormat.Type.JSON_SCHEMA)
            .jsonSchema(JsonUtils.stringify(schemaMap))
            .build();
    }

    /**
     * 生成 JSON Schema 节点
     *
     * @param targetClass 目标类
     * @return JSON Schema 的 JsonNode 表示
     */
    public static JsonNode buildSchemaNode(Class<?> targetClass) {
        return buildGenerator().generateSchema(targetClass);
    }

    /**
     * 构建工具 inputSchema 字符串
     *
     * @param targetClass 工具入参类
     * @return JSON Schema 字符串，可直接用于 DefaultToolDefinition.Builder#inputSchema
     */
    public static String buildInputSchema(Class<?> targetClass) {
        return buildInputSchemaGenerator().generateSchema(targetClass).toString();
    }

    /**
     * 构建无入参工具的 inputSchema 字符串
     *
     * @return 空对象 JSON Schema 字符串
     */
    public static String buildEmptyInputSchema() {
        return "{\"type\":\"object\",\"properties\":{}}";
    }

    // ================================ private 方法 ================================

    /**
     * 生成 JSON Schema Map
     *
     * @param targetClass 目标类
     * @return JSON Schema 的 Map 表示
     */
    private static Map<String, Object> buildSchemaMap(Class<?> targetClass) {
        JsonNode jsonNode = buildGenerator().generateSchema(targetClass);

        return JsonUtils.convert(jsonNode, new TypeReference<>() {});
    }

    /**
     * 构建结构化输出 SchemaGenerator
     */
    private static SchemaGenerator buildGenerator() {
        // 配置 Jackson 模块，使 schema 生成与项目的 Jackson 序列化行为保持一致
        JacksonSchemaModule jacksonModule = new JacksonSchemaModule(
            // 遵循 @JsonProperty(required=true) 注解，将对应字段标记为 schema 必填项
            JacksonOption.RESPECT_JSONPROPERTY_REQUIRED,
            // 遵循 @JsonPropertyOrder 注解，按声明顺序排列 schema 字段
            JacksonOption.RESPECT_JSONPROPERTY_ORDER,
            // 枚举直接使用 @JsonValue 的序列化值，避免生成后手动修正枚举约束
            JacksonOption.FLATTENED_ENUMS_FROM_JSONVALUE
        );

        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
                // DRAFT_2020_12 是当前最新稳定版 JSON Schema 规范，DashScope 支持该版本
                SchemaVersion.DRAFT_2020_12,
                // PLAIN_JSON 不预设任何额外选项，由后续 with() 调用精确控制行为
                OptionPreset.PLAIN_JSON)
            .with(jacksonModule)
            // 禁止 schema 中出现未定义的额外属性，确保大模型输出严格匹配目标类结构
            .with(Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT);

        // 将所有字段标记为 required，确保大模型输出不遗漏任何字段
        configBuilder.forFields().withRequiredCheck(f -> true);

        return new SchemaGenerator(configBuilder.build());
    }

    /**
     * 构建工具 inputSchema SchemaGenerator
     */
    private static SchemaGenerator buildInputSchemaGenerator() {
        // 配置 Jackson 模块，使 schema 生成与项目的 Jackson 序列化行为保持一致
        JacksonSchemaModule jacksonModule = new JacksonSchemaModule(
            // 遵循 @JsonProperty(required=true) 注解，通过注解显式控制哪些字段为必填
            JacksonOption.RESPECT_JSONPROPERTY_REQUIRED,
            // 遵循 @JsonPropertyOrder 注解，按声明顺序排列 schema 字段
            JacksonOption.RESPECT_JSONPROPERTY_ORDER,
            // 枚举直接使用 @JsonValue 的序列化值，避免生成后手动修正枚举约束
            JacksonOption.FLATTENED_ENUMS_FROM_JSONVALUE
        );

        // 配置 Swagger 模块，支持 @Schema 注解携带 description、minimum、maximum 等约束
        Swagger2Module swagger2Module = new Swagger2Module();

        return new SchemaGenerator(new SchemaGeneratorConfigBuilder(
                SchemaVersion.DRAFT_2020_12,
                OptionPreset.PLAIN_JSON)
            .with(jacksonModule)
            .with(swagger2Module)
            .build());
    }
}
