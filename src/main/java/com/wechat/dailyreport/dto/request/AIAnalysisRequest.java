package com.wechat.dailyreport.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * AI分析请求DTO
 */
@Data
@Accessors(chain = true)
public class AIAnalysisRequest {
    
    /**
     * AI模型名称
     */
    private String model;
    
    /**
     * 消息列表
     */
    private List<AIMessage> messages;
    
    /**
     * 温度参数，控制输出的随机性
     */
    private Double temperature;
    
    /**
     * 最大输出令牌数
     */
    private Integer maxTokens;
    
    /**
     * AI消息
     */
    @Data
    @Accessors(chain = true)
    public static class AIMessage {
        
        /**
         * 角色：system、user、assistant
         */
        private String role;
        
        /**
         * 消息内容
         */
        private String content;
    }
}