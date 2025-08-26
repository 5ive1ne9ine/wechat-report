package com.wechat.dailyreport.service;

import com.wechat.dailyreport.client.AIServiceClient;
import com.wechat.dailyreport.dto.config.AIServiceConfig;
import com.wechat.dailyreport.dto.request.AIAnalysisRequest;
import com.wechat.dailyreport.dto.response.AIAnalysisResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * AI服务
 */
@Service
@Slf4j
public class AIService {
    
    @Autowired
    private AIServiceClient aiServiceClient;
    
    @Autowired
    private AIConfigService aiConfigService;
    
    /**
     * 结构化分析聊天数据
     * 
     * @param chatData 原始聊天数据
     * @return 结构化分析结果
     */
    public String structureAnalysis(String chatData) {
        log.info("开始进行结构化分析");
        
        AIServiceConfig config = aiConfigService.getAIConfig();
        String systemPrompt = getStructurePrompt();
        
        AIAnalysisRequest request = buildRequest(config, systemPrompt, chatData);
        AIAnalysisResponse response = callAIService(config, request);
        
        String result = extractContent(response);
        log.info("结构化分析完成，结果长度: {}", result.length());
        
        return result;
    }
    
    /**
     * 生成最终报告
     * 
     * @param structuredData 结构化数据
     * @return 最终报告
     */
    public String generateReport(String structuredData) {
        log.info("开始生成最终报告");
        
        AIServiceConfig config = aiConfigService.getAIConfig();
        String systemPrompt = getReportPrompt();
        
        AIAnalysisRequest request = buildRequest(config, systemPrompt, structuredData);
        AIAnalysisResponse response = callAIService(config, request);
        
        String result = extractContent(response);
        log.info("最终报告生成完成，结果长度: {}", result.length());
        
        return result;
    }
    
    /**
     * 构建AI请求
     */
    private AIAnalysisRequest buildRequest(AIServiceConfig config, String systemPrompt, String userContent) {
        List<AIAnalysisRequest.AIMessage> messages = Arrays.asList(
                new AIAnalysisRequest.AIMessage().setRole("system").setContent(systemPrompt),
                new AIAnalysisRequest.AIMessage().setRole("user").setContent(userContent)
        );
        
        return new AIAnalysisRequest()
                .setModel(config.getModel())
                .setMessages(messages)
                .setTemperature(config.getTemperature())
                .setMaxTokens(config.getMaxTokens());
    }
    
    /**
     * 调用AI服务
     */
    private AIAnalysisResponse callAIService(AIServiceConfig config, AIAnalysisRequest request) {
        if (StringUtils.isBlank(config.getApiKey())) {
            throw new RuntimeException("AI服务API密钥未配置");
        }
        
        // 设置客户端 URL
        aiServiceClient.setBaseUrl(config.getBaseUrl());
        
        String authorization = "Bearer " + config.getApiKey();
        
        try {
            return aiServiceClient.analyze(authorization, request);
        } catch (Exception e) {
            log.error("调用AI服务失败", e);
            throw new RuntimeException("调用AI服务失败: " + e.getMessage());
        }
    }
    
    /**
     * 从AI响应中提取内容
     */
    private String extractContent(AIAnalysisResponse response) {
        if (response == null || 
            response.getChoices() == null || 
            response.getChoices().isEmpty()) {
            throw new RuntimeException("AI服务返回空响应");
        }
        
        AIAnalysisResponse.AIChoice choice = response.getChoices().get(0);
        if (choice.getMessage() == null || 
            StringUtils.isBlank(choice.getMessage().getContent())) {
            throw new RuntimeException("AI服务返回空内容");
        }
        
        return choice.getMessage().getContent().trim();
    }
    
    /**
     * 获取结构化分析提示词
     */
    private String getStructurePrompt() {
        return "你是一个专业的聊天数据分析师。请分析以下微信聊天记录，并输出结构化的JSON数据。\n\n" +
               "请按照以下格式分析：\n" +
               "{\n" +
               "  \"summary\": {\n" +
               "    \"total_messages\": \"总消息数\",\n" +
               "    \"participants\": [\"参与者列表\"],\n" +
               "    \"time_range\": \"时间范围\",\n" +
               "    \"main_topics\": [\"主要话题列表\"]\n" +
               "  },\n" +
               "  \"sentiment_analysis\": {\n" +
               "    \"overall_sentiment\": \"整体情感倾向(积极/中性/消极)\",\n" +
               "    \"emotional_highlights\": [\"情感亮点\"]\n" +
               "  },\n" +
               "  \"interaction_patterns\": {\n" +
               "    \"most_active_participant\": \"最活跃参与者\",\n" +
               "    \"response_patterns\": \"回复模式分析\",\n" +
               "    \"conversation_flow\": \"对话流程特点\"\n" +
               "  },\n" +
               "  \"key_events\": [\n" +
               "    {\n" +
               "      \"time\": \"时间\",\n" +
               "      \"event\": \"关键事件描述\",\n" +
               "      \"participants\": [\"相关参与者\"]\n" +
               "    }\n" +
               "  ]\n" +
               "}\n\n" +
               "请确保输出格式为有效的JSON，不要包含任何额外的解释文字。";
    }
    
    /**
     * 获取报告生成提示词
     */
    private String getReportPrompt() {
        return "你是一个专业的聊天分析报告撰写专家。基于以下结构化的聊天分析数据，生成一份详细、专业且易读的分析报告。\n\n" +
               "报告要求：\n" +
               "1. 使用HTML格式，包含适当的标题、段落和列表\n" +
               "2. 报告应包含以下部分：\n" +
               "   - 执行摘要\n" +
               "   - 聊天概况\n" +
               "   - 参与者分析\n" +
               "   - 话题与内容分析\n" +
               "   - 情感与氛围分析\n" +
               "   - 互动模式分析\n" +
               "   - 关键事件回顾\n" +
               "   - 总结与建议\n" +
               "3. 语言要专业但通俗易懂\n" +
               "4. 使用中文撰写\n" +
               "5. 适当使用HTML标签进行格式化（如<h2>, <h3>, <p>, <ul>, <li>, <strong>等）\n" +
               "6. 报告长度控制在1000-2000字\n\n" +
               "请基于提供的结构化数据生成报告，不要编造不存在的信息。";
    }
}