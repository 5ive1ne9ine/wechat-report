package com.wechat.dailyreport.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用常量类
 */
public interface AppConstants {
    
    
    /**
     * 服务类型常量
     */
    interface ServiceType {
        String AI_SERVICE = "AI_SERVICE";
        String CHATLOG_SERVICE = "CHATLOG_SERVICE";
    }
    
    /**
     * 报告状态常量
     */
    interface ReportStatus {
        String PROCESSING = "PROCESSING";
        String COMPLETED = "COMPLETED";
        String FAILED = "FAILED";
    }
    
    /**
     * 聊天类型常量
     */
    interface ChatType {
        String GROUP = "GROUP";
        String PRIVATE = "PRIVATE";
    }
    
    /**
     * 消息类型常量
     */
    interface MessageType {
        String TEXT = "TEXT";
        String IMAGE = "IMAGE";
        String FILE = "FILE";
        String VOICE = "VOICE";
        String VIDEO = "VIDEO";

        /**
         * 1 文本消息
         */
        @Getter
        @AllArgsConstructor
        enum Type {
            TEXT(1, "文本"),
            ;
            private Integer code;
            private String desc;
        }

    }
    
    /**
     * AI角色常量
     */
    interface AIRole {
        String SYSTEM = "system";
        String USER = "user";
        String ASSISTANT = "assistant";
    }
    
    /**
     * 缓存键常量
     */
    interface CacheKey {
        String CHAT_SESSIONS = "chatSessions";
        String AI_CONFIG = "aiConfig";
        String CHATLOG_CONFIG = "chatlogConfig";
    }
    
    /**
     * 配置键常量
     */
    interface ConfigKey {
        // AI服务配置键
        String AI_MODEL = "model";
        String AI_BASE_URL = "base_url";
        String AI_API_KEY = "api_key";
        String AI_TIMEOUT = "timeout";
        String AI_TEMPERATURE = "temperature";
        String AI_MAX_TOKENS = "max_tokens";
        
        // Chatlog服务配置键
        String CHATLOG_BASE_URL = "base_url";
        String CHATLOG_TIMEOUT = "timeout";
    }
}