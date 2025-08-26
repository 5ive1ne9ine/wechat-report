package com.wechat.dailyreport.service;

import com.wechat.dailyreport.dto.config.ChatlogServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Chatlog服务配置管理服务
 */
@Service
@Slf4j
public class ChatlogConfigService {
    
    @Value("${app.chatlog.default.base-url:http://127.0.0.1:5030}")
    private String defaultBaseUrl;
    
    @Value("${app.chatlog.default.timeout:30000}")
    private Integer defaultTimeout;
    
    // 使用内存存储配置更改（生产环境应该使用数据库）
    private final Map<String, String> configOverrides = new ConcurrentHashMap<>();
    
    /**
     * 获取Chatlog服务配置
     */
    public ChatlogServiceConfig getChatlogConfig() {
        return new ChatlogServiceConfig()
                .setBaseUrl(getConfigValue("base_url", defaultBaseUrl))
                .setTimeout(Integer.parseInt(getConfigValue("timeout", String.valueOf(defaultTimeout))));
    }
    
    /**
     * 更新Chatlog服务配置
     */
    public void updateChatlogConfig(ChatlogServiceConfig config) {
        log.info("更新Chatlog服务配置: {}", config);
        
        saveConfigValue("base_url", config.getBaseUrl());
        saveConfigValue("timeout", String.valueOf(config.getTimeout()));
        
        log.info("Chatlog服务配置更新完成");
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