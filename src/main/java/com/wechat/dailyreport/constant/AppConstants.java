package com.wechat.dailyreport.constant;

/**
 * 应用常量类
 */
public final class AppConstants {
    
    private AppConstants() {
        // 工具类，不允许实例化
    }
    
    /**
     * 服务类型常量
     */
    public static final class ServiceType {
        public static final String AI_SERVICE = "AI_SERVICE";
        public static final String CHATLOG_SERVICE = "CHATLOG_SERVICE";
    }
    
    /**
     * 报告状态常量
     */
    public static final class ReportStatus {
        public static final String PROCESSING = "PROCESSING";
        public static final String COMPLETED = "COMPLETED";
        public static final String FAILED = "FAILED";
    }
    
    /**
     * 聊天类型常量
     */
    public static final class ChatType {
        public static final String GROUP = "GROUP";
        public static final String PRIVATE = "PRIVATE";
    }
    
    /**
     * 消息类型常量
     */
    public static final class MessageType {
        public static final String TEXT = "TEXT";
        public static final String IMAGE = "IMAGE";
        public static final String FILE = "FILE";
        public static final String VOICE = "VOICE";
        public static final String VIDEO = "VIDEO";
    }
    
    /**
     * AI角色常量
     */
    public static final class AIRole {
        public static final String SYSTEM = "system";
        public static final String USER = "user";
        public static final String ASSISTANT = "assistant";
    }
    
    /**
     * 缓存键常量
     */
    public static final class CacheKey {
        public static final String CHAT_SESSIONS = "chatSessions";
        public static final String AI_CONFIG = "aiConfig";
        public static final String CHATLOG_CONFIG = "chatlogConfig";
    }
    
    /**
     * 配置键常量
     */
    public static final class ConfigKey {
        // AI服务配置键
        public static final String AI_MODEL = "model";
        public static final String AI_BASE_URL = "base_url";
        public static final String AI_API_KEY = "api_key";
        public static final String AI_TIMEOUT = "timeout";
        public static final String AI_TEMPERATURE = "temperature";
        public static final String AI_MAX_TOKENS = "max_tokens";
        
        // Chatlog服务配置键
        public static final String CHATLOG_BASE_URL = "base_url";
        public static final String CHATLOG_TIMEOUT = "timeout";
    }
}