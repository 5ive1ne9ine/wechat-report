package com.wechat.dailyreport.client;

import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.wechat.dailyreport.config.AiConfig;
import com.wechat.dailyreport.dto.request.AIAnalysisRequest;
import com.wechat.dailyreport.dto.response.AIAnalysisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI服务客户端（使用 OpenAI Java SDK）
 */
@Component
@Slf4j
public class AIServiceClient {

    @Autowired
    private AiConfig aiConfig;

    /**
     * 调用AI分析接口
     *
     * @param apiKey  API密钥
     * @param baseUrl 基础URL
     * @param request AI分析请求
     * @return AI分析响应
     */
    public AIAnalysisResponse analyze(String apiKey, String baseUrl, AIAnalysisRequest request) {
        log.info("调用AI服务: {}", baseUrl);

        try {
            // 创建 OpenAI 客户端
            OpenAIClient client = aiConfig.createOpenAIClient(apiKey, baseUrl);

            // 构建请求参数
            ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams.builder()
                    .model(request.getModel());

            // 添加消息
            for (AIAnalysisRequest.AIMessage message : request.getMessages()) {
                switch (message.getRole().toLowerCase()) {
                    case "system":
                        paramsBuilder.addSystemMessage(message.getContent());
                        break;
                    case "user":
                        paramsBuilder.addUserMessage(message.getContent());
                        break;
                    case "assistant":
                        paramsBuilder.addAssistantMessage(message.getContent());
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported role: " + message.getRole());
                }
            }

            // 设置可选参数
            if (request.getTemperature() != null) {
                paramsBuilder.temperature(request.getTemperature());
            }
            if (request.getMaxTokens() != null) {
                paramsBuilder.maxTokens(request.getMaxTokens());
            }

            // 发起请求
            ChatCompletion chatCompletion = client.chat().completions().create(paramsBuilder.build());

            // 转换响应格式
            return convertToAIAnalysisResponse(chatCompletion);

        } catch (Exception e) {
            log.error("调用AI服务失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用AI服务失败: " + e.getMessage());
        }
    }

    /**
     * 转换 OpenAI 响应为自定义格式
     */
    private AIAnalysisResponse convertToAIAnalysisResponse(ChatCompletion chatCompletion) {
        AIAnalysisResponse response = new AIAnalysisResponse()
                .setId(chatCompletion.id())
                .setObject("chat.completion") // SDK可能没有object()方法，使用固定值
                .setCreated(chatCompletion.created())
                .setModel(chatCompletion.model());

        // 转换 choices
        if (chatCompletion.choices() != null && !chatCompletion.choices().isEmpty()) {
            List<AIAnalysisResponse.AIChoice> choices = chatCompletion.choices().stream()
                    .map(choice -> {
                        AIAnalysisResponse.AIMessage message = new AIAnalysisResponse.AIMessage()
                                .setRole("assistant") // 使用固定值，因为响应总是assistant
                                .setContent(choice.message().content().orElse(""));

                        return new AIAnalysisResponse.AIChoice()
                                .setIndex((int) choice.index()) // 将long转换为int
                                .setMessage(message)
                                .setFinishReason(choice.finishReason() != null ? choice.finishReason().toString() : null);
                    })
                    .collect(Collectors.toList());
            response.setChoices(choices);
        }

        // 转换 usage
        if (chatCompletion.usage() != null && chatCompletion.usage().isPresent()) {
            com.openai.models.completions.CompletionUsage usage = chatCompletion.usage().get();
            AIAnalysisResponse.AIUsage aiUsage = new AIAnalysisResponse.AIUsage()
                    .setPromptTokens((int) usage.promptTokens())
                    .setCompletionTokens((int) usage.completionTokens())
                    .setTotalTokens((int) usage.totalTokens());
            response.setUsage(aiUsage);
        }

        return response;
    }
}