package com.weutil.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * JSON 服务
 *
 * <h2>说明
 * <p>封装通用的 JSON 序列化和反序列化方法
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 **/
@Service
@RequiredArgsConstructor
public class JsonService {

    /** JSON 序列化映射器 */
    private final JsonMapper jsonMapper;

    /**
     * JSON 序列化
     *
     * <h3>序列化方法
     * <p>将 Java 对象序列化为 JSON 字符串。
     *
     * @param object 待序列化的对象
     * @return JSON 字符串
     */
    public String stringify(Object object) {
        return jsonMapper.writeValueAsString(object);
    }

    /**
     * JSON 反序列化
     *
     * <h3>解析方法
     * <p>将 JSON 字符串解析为指定的 Java 对象类型。
     *
     * @param jsonString JSON 字符串
     * @param valueType  目标对象的 Class 类型
     * @param <T>        目标对象类型
     * @return 解析后的对象
     */
    public <T> T parse(String jsonString, Class<T> valueType) {
        return jsonMapper.readValue(jsonString, valueType);
    }

    /**
     * JSON 节点转换
     *
     * <h3>转换方法
     * <p>将 JsonNode 对象转换为指定的 Java 对象类型。
     *
     * @param jsonNode  JSON 节点对象
     * @param valueType 目标对象的 Class 类型
     * @param <T>       目标对象类型
     * @return 转换后的对象
     */
    public <T> T convert(JsonNode jsonNode, Class<T> valueType) {
        return jsonMapper.treeToValue(jsonNode, valueType);
    }

    /**
     * JSON 字符串解析为 JsonNode
     *
     * <h3>解析方法
     * <p>将 JSON 字符串解析为 JsonNode 树结构。
     *
     * @param jsonString JSON 字符串
     * @return JsonNode 对象
     */
    public JsonNode readTree(String jsonString) {
        return jsonMapper.readTree(jsonString);
    }
}
