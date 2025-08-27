package com.wechat.dailyreport.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类
 */
@Configuration
public class AiConfig {

    /**
     * 创建 OpenAI 客户端
     * 注意：这个Bean不能直接使用，因为需要动态配置
     * 实际使用时在 AIServiceClient 中动态创建
     */
    public OpenAIClient createOpenAIClient(String apiKey, String baseUrl) {
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }
}
