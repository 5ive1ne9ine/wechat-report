package com.wechat.dailyreport.service;

import com.wechat.dailyreport.dto.config.AIServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI服务配置管理服务
 */
@Service
@Slf4j
public class AIConfigService {
    
    @Value("${app.ai.default.model:gpt-3.5-turbo}")
    private String defaultModel;
    
    @Value("${app.ai.default.base-url:https://api.openai.com}")
    private String defaultBaseUrl;
    
    @Value("${app.ai.default.api-key:}")
    private String defaultApiKey;
    
    @Value("${app.ai.default.timeout:60000}")
    private Integer defaultTimeout;
    
    @Value("${app.ai.default.temperature:0.7}")
    private Double defaultTemperature;
    
    @Value("${app.ai.default.max-tokens:4000}")
    private Integer defaultMaxTokens;
    
    // 使用内存存储配置更改（生产环境应该使用数据库）
    private final Map<String, String> configOverrides = new ConcurrentHashMap<>();
    
    /**
     * 获取AI服务配置
     */
    public AIServiceConfig getAIConfig() {
        return new AIServiceConfig()
                .setModel(getConfigValue("model", defaultModel))
                .setBaseUrl(getConfigValue("base_url", defaultBaseUrl))
                .setApiKey(getConfigValue("api_key", defaultApiKey))
                .setTimeout(Integer.parseInt(getConfigValue("timeout", String.valueOf(defaultTimeout))))
                .setTemperature(Double.parseDouble(getConfigValue("temperature", String.valueOf(defaultTemperature))))
                .setMaxTokens(Integer.parseInt(getConfigValue("max_tokens", String.valueOf(defaultMaxTokens))));
    }
    
    /**
     * 更新AI服务配置
     */
    public void updateAIConfig(AIServiceConfig config) {
        log.info("更新AI服务配置: {}", config);
        
        saveConfigValue("model", config.getModel());
        saveConfigValue("base_url", config.getBaseUrl());
        saveConfigValue("api_key", config.getApiKey());
        saveConfigValue("timeout", String.valueOf(config.getTimeout()));
        saveConfigValue("temperature", String.valueOf(config.getTemperature()));
        saveConfigValue("max_tokens", String.valueOf(config.getMaxTokens()));
        
        log.info("AI服务配置更新完成");
    }
    
    /**
     * 获取配置值
     */
    private String getConfigValue(String configKey, String defaultValue) {
        String overrideValue = configOverrides.get(configKey);
        return overrideValue != null ? overrideValue : defaultValue;
    }
    
    /**
     * 保存配置值
     */
    private void saveConfigValue(String configKey, String configValue) {
        configOverrides.put(configKey, configValue);
        log.info("配置已保存: {} = {}", configKey, configValue);
    }
}