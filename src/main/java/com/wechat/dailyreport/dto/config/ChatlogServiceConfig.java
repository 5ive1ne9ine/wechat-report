package com.wechat.dailyreport.dto.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Chatlog服务配置DTO
 */
@Data
@Accessors(chain = true)
public class ChatlogServiceConfig {
    
    /**
     * 基础URL
     */
    private String baseUrl;
    
    /**
     * 超时时间(毫秒)
     */
    private Integer timeout;
}