package com.weutil.common.support.ai;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 停止原因 Advisor
 *
 * <h2>功能说明
 * <p>将 {@code Generation.getMetadata()} 中的 {@code finishReason}（停止原因）合入 {@code AssistantMessage.getMetadata()}，
 * <p>以便 {@code ChatMemory.add} 保存消息时能够获取该信息。
 *
 * <h2>数据流说明
 * <p>{@code finishReason} 存在于 {@code Generation.getMetadata()} 中，而非 {@code AssistantMessage.getMetadata()}。
 * <p>本 Advisor 在 {@code after()} 方法中重建 {@code AssistantMessage}，将 {@code finishReason} 合入其 metadata。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class FinishReasonAdvisor implements BaseAdvisor {

    /** Advisor 执行顺序，设置为 0 确保在 ChatModelCallAdvisor 之前执行 */
    private static final int ORDER = 0;

    // ================================ public 方法 ================================

    /**
     * 获取 Advisor 执行顺序
     *
     * <h3>执行顺序说明
     * <p>设置为 0，确保在 ChatModelCallAdvisor（order=LOWEST_PRECEDENCE）之前执行。
     * <p>这样可以捕获请求在发送到 ChatModel 之前的最终状态。
     *
     * @return 执行顺序值
     */
    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * 在调用前处理请求
     *
     * <h3>方法说明
     * <p>不修改请求，直接返回原请求。
     *
     * @param chatClientRequest 聊天客户端请求
     * @param advisorChain      Advisor 链
     * @return 原请求
     */
    @Override
    public ChatClientRequest before(
        ChatClientRequest chatClientRequest,
        AdvisorChain advisorChain
    ) {
        return chatClientRequest;
    }

    /**
     * 在调用后处理响应
     *
     * <h3>核心逻辑
     * <p>将 {@code Generation.getMetadata()} 中的 {@code finishReason} 合入 {@code AssistantMessage.getMetadata()}。
     * <p>重建 {@code AssistantMessage} 后，重建整个 {@code ChatClientResponse} 返回。
     *
     * <h3>数据结构说明
     * <pre>
     * ChatResponse
     * ├── getResults() → List&lt;Generation&gt;
     * │   └── Generation
     * │       ├── getOutput() → AssistantMessage
     * │       │   └── getMetadata() → Map&lt;String, Object&gt; ← 消息 metadata（不含 finishReason）
     * │       └── getMetadata() → ChatGenerationMetadata
     * │           └── getFinishReason() → String ← 停止原因
     * </pre>
     *
     * @param chatClientResponse 聊天客户端响应
     * @param advisorChain       Advisor 链
     * @return 处理后的响应（AssistantMessage.metadata 已包含 finishReason）
     */
    @Override
    public ChatClientResponse after(
        ChatClientResponse chatClientResponse,
        AdvisorChain advisorChain
    ) {
        ChatResponse chatResponse = chatClientResponse.chatResponse();

        // 若无响应，直接返回
        if (chatResponse == null) {
            return chatClientResponse;
        }

        ChatResponseMetadata responseMetadata = chatResponse.getMetadata();

        // 重建 Generation 列表，将 finishReason 合入 AssistantMessage.metadata
        List<Generation> enrichedGenerations = new ArrayList<>();
        for (Generation generation : chatResponse.getResults()) {
            AssistantMessage originalMsg = generation.getOutput();
            ChatGenerationMetadata generationMetadata = generation.getMetadata();
            String finishReason = generationMetadata.getFinishReason();

            // 合入 finishReason 到 AssistantMessage.metadata
            Map<String, Object> enrichedMetadata = new HashMap<>(originalMsg.getMetadata());
            if (finishReason != null) {
                enrichedMetadata.put("finishReason", finishReason);
            }

            // 重建 AssistantMessage，设置合入后的 metadata
            AssistantMessage enrichedMsg = AssistantMessage.builder()
                .content(originalMsg.getText() != null ? originalMsg.getText() : "")
                .properties(enrichedMetadata)
                .toolCalls(originalMsg.getToolCalls())
                .media(originalMsg.getMedia())
                .build();

            // 重建 Generation，保留原有的 ChatGenerationMetadata
            Generation enrichedGeneration = new Generation(enrichedMsg, generationMetadata);
            enrichedGenerations.add(enrichedGeneration);
        }

        // 重建 ChatResponse
        ChatResponse enrichedChatResponse = ChatResponse.builder()
            .generations(enrichedGenerations)
            .metadata(responseMetadata)
            .build();

        // 重建 ChatClientResponse，保留原有的 context
        return ChatClientResponse.builder()
            .chatResponse(enrichedChatResponse)
            .context(chatClientResponse.context())
            .build();
    }
}
