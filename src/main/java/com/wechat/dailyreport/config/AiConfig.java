package com.wechat.dailyreport.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: caihh
 * @CreateTime: 2025/8/26-15:40
 * @Description:
 */
@Configuration
public class AiConfig {

    @Bean
    public OpenAIClient wenXinYiYanopenAIClient() {
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey("bce-v3/ALTAK-um46S332RQuEXjAurBr9F/26d93992b9308948c6eb64ecac4355865a4bb7dc")
                .baseUrl("https://qianfan.baidubce.com/v2/")
                .build();
        return client;
    }

}
