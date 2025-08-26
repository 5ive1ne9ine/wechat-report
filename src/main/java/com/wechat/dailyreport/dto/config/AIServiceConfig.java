package com.wechat.dailyreport.dto.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * AI服务配置DTO
 */
@Data
@Accessors(chain = true)
public class AIServiceConfig {
    
    /**
     * AI模型名称
     */
    private String model;
    
    /**
     * 基础URL
     */
    private String baseUrl;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 超时时间(毫秒)
     */
    private Integer timeout;
    
    /**
     * 温度参数
     */
    private Double temperature;
    
    /**
     * 最大令牌数
     */
    private Integer maxTokens;
}