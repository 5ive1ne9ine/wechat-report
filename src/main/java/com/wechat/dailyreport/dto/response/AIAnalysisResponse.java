package com.wechat.dailyreport.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * AI分析响应DTO
 */
@Data
@Accessors(chain = true)
public class AIAnalysisResponse {
    
    /**
     * 响应ID
     */
    private String id;
    
    /**
     * 对象类型
     */
    private String object;
    
    /**
     * 创建时间戳
     */
    private Long created;
    
    /**
     * 使用的模型
     */
    private String model;
    
    /**
     * 选择列表
     */
    private List<AIChoice> choices;
    
    /**
     * 使用情况统计
     */
    private AIUsage usage;
    
    /**
     * AI选择
     */
    @Data
    @Accessors(chain = true)
    public static class AIChoice {
        
        /**
         * 选择索引
         */
        private Integer index;
        
        /**
         * 消息内容
         */
        private AIMessage message;
        
        /**
         * 结束原因
         */
        private String finishReason;
    }
    
    /**
     * AI消息
     */
    @Data
    @Accessors(chain = true)
    public static class AIMessage {
        
        /**
         * 角色
         */
        private String role;
        
        /**
         * 内容
         */
        private String content;
    }
    
    /**
     * 使用情况统计
     */
    @Data
    @Accessors(chain = true)
    public static class AIUsage {
        
        /**
         * 提示令牌数
         */
        private Integer promptTokens;
        
        /**
         * 完成令牌数
         */
        private Integer completionTokens;
        
        /**
         * 总令牌数
         */
        private Integer totalTokens;
    }
}