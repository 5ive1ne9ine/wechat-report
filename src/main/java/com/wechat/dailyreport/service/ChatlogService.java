package com.wechat.dailyreport.service;

import com.wechat.dailyreport.client.ChatlogClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Chatlog服务
 */
@Service
@Slf4j
public class ChatlogService {
    
    @Autowired
    private ChatlogClient chatlogClient;
    
    @Autowired
    private ChatlogConfigService chatlogConfigService;
    
    // 使用内存存储聊天会话（生产环境应该使用数据库）
    private final Map<String, ChatSession> sessionCache = new ConcurrentHashMap<>();
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * 聊天会话数据类
     */
    @Data
    public static class ChatSession {
        private Long id;
        private String chatId;
        private String chatName;
        private String chatType;
        private Integer memberCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    /**
     * 聊天消息数据类
     */
    @Data
    public static class ChatMessage {
        private Long id;
        private String messageId;
        private String chatId;
        private String senderId;
        private String senderName;
        private String messageType;
        private String content;
        private LocalDateTime timestamp;
        private LocalDateTime createdAt;
    }
    
    /**
     * 获取所有聊天会话
     */
    public List<ChatSession> getAllChatSessions() {
        log.info("获取所有聊天会话");
        
        try {
            // 设置客户端 URL
            chatlogClient.setBaseUrl(chatlogConfigService.getChatlogConfig().getBaseUrl());
            
            List<ChatSession> sessions = chatlogClient.getChatSessions();
            log.info("从 Chatlog服务获取到 {} 个聊天会话", sessions.size());
            
            // 缓存到内存
            saveChatSessions(sessions);
            
            return sessions;
        } catch (Exception e) {
            log.error("从 Chatlog服务获取会话失败，尝试从内存缓存获取", e);
            
            // 如果远程服务不可用，从内存缓存获取
            return sessionCache.values().stream().collect(Collectors.toList());
        }
    }
    
    /**
     * 根据聊天ID获取聊天会话
     */
    public ChatSession getChatSessionById(String chatId) {
        log.info("获取聊天会话: {}", chatId);
        
        ChatSession session = sessionCache.get(chatId);
        
        if (session == null) {
            // 如果内存没有，尝试从远程获取
            List<ChatSession> allSessions = getAllChatSessions();
            session = allSessions.stream()
                    .filter(s -> chatId.equals(s.getChatId()))
                    .findFirst()
                    .orElse(null);
        }
        
        return session;
    }
    
    /**
     * 获取指定日期的聊天消息
     */
    public List<ChatMessage> getChatMessages(String chatId, LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        log.info("获取聊天消息: chatId={}, date={}", chatId, dateStr);
        
        try {
            List<ChatMessage> messages = chatlogClient.getChatMessages(chatId, dateStr);
            log.info("从 Chatlog服务获取到 {} 条消息", messages.size());
            
            return messages;
        } catch (Exception e) {
            log.error("从 Chatlog服务获取消息失败: chatId={}, date={}", chatId, dateStr, e);
            throw new RuntimeException("获取聊天消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存聊天会话到内存缓存
     */
    private void saveChatSessions(List<ChatSession> sessions) {
        try {
            for (ChatSession session : sessions) {
                sessionCache.put(session.getChatId(), session);
            }
            
            log.info("已缓存 {} 个聊天会话到内存", sessions.size());
        } catch (Exception e) {
            log.error("缓存聊天会话到内存失败", e);
        }
    }
}