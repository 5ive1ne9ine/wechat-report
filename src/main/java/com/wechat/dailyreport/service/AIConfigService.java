package com.wechat.dailyreport.service;

import com.wechat.dailyreport.dto.config.AIServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI服务配置管理服务
 */
@Service
@Slf4j
public class AIConfigService {

    // 使用内存存储配置更改（生产环境应该使用数据库）
    private final Map<String, String> configOverrides = new ConcurrentHashMap<>();

    /**
     * 获取AI服务配置
     */
    public AIServiceConfig getAIConfig() {
        return new AIServiceConfig()
                .setModel(getConfigValue("model", "ernie-4.5-turbo-128k"))
                .setBaseUrl(getConfigValue("base_url", "https://qianfan.baidubce.com/v2/"))
                .setApiKey(getConfigValue("api_key", StringUtils.EMPTY))
                .setTimeout(Integer.parseInt(getConfigValue("timeout", "60000")))
                .setTemperature(Double.parseDouble(getConfigValue("temperature", "0.7")))
                .setMaxTokens(Integer.parseInt(getConfigValue("max_tokens", "12288")));
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