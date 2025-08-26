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
import java.util.Collections;
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
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ChatSession getChatSessionByNiceName(String niceName) {
        return null;
    }

    /**
     * 聊天会话数据类
     */
    @Data
    public static class ChatSession {
        private String name;
        private String owner;
        private String remark;
        private String nickName;
        private List<ChatSessionUser> users;
    }

    /**
     * 聊天会话-群聊用户数据类
     */
    @Data
    public static class ChatSessionUser {
        private String userName;
        private String displayName;
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
     * 获取所有群聊会话
     */
    public List<ChatSession> getAllChatSessions() {
        log.info("获取所有聊天会话");
        
        try {
            // 设置客户端 URL
            chatlogClient.setBaseUrl(chatlogConfigService.getChatlogConfig().getBaseUrl());
            
            List<ChatSession> sessions = chatlogClient.getGroupChatSessionsByNiceName(null);
            log.info("从 Chatlog服务获取到 {} 个聊天会话", sessions.size());
            
            return sessions;
        } catch (Exception e) {
            log.error("从 Chatlog服务获取会话失败，尝试从内存缓存获取", e);
        }
        return null;
    }
    
    /**
     * 根据群聊名称搜索群聊会话
     * 
     * @param groupName 群聊名称（支持模糊匹配，可为空字符串返回所有群聊）
     * @return 匹配的群聊会话列表
     */
    public List<ChatSession> searchGroupChatsByName(String groupName) {
        // 对于空字符串或null，传空字符串给客户端以获取所有群聊
        String searchKeyword = (groupName == null || groupName.trim().isEmpty()) ? "" : groupName.trim();
        log.info("根据名称搜索群聊: groupName={}, searchKeyword={}", groupName, searchKeyword);
        try {
            // 设置Client URL
            chatlogClient.setBaseUrl(chatlogConfigService.getChatlogConfig().getBaseUrl());
            // 调用客户端方法
            List<ChatSession> groups = chatlogClient.getGroupChatSessionsByNiceName(searchKeyword);
            log.info("搜索到 {} 个匹配的群聊", groups != null ? groups.size() : 0);
            
            return groups != null ? groups : Collections.emptyList();
        } catch (Exception e) {
            log.error("搜索群聊失败: groupName={}", groupName, e);
            throw new RuntimeException("搜索群聊失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取私聊会话列表（从所有会话中筛选）
     * 
     * @return 私聊会话列表
     */
    public List<ChatSession> getPrivateChatSessions() {
        List<ChatSession> allSessions = getAllChatSessions();
        if (allSessions == null) {
            return Collections.emptyList();
        }
        
        // 根据业务规则筛选私聊会话
        // 这里需要根据ChatSession的实际结构来判断是否为私聊
        return allSessions.stream()
                .filter(session -> isPrivateChat(session))
                .collect(Collectors.toList());
    }
    
    /**
     * 判断是否为私聊会话
     * 
     * @param session 聊天会话
     * @return 是否为私聊
     */
    private boolean isPrivateChat(ChatSession session) {
        // 根据实际的ChatSession结构来判断
        // 可能的判断条件：用户数量、聊天类型字段等
        return session.getUsers() != null && session.getUsers().size() == 2;
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
     * 获取指定日期范围内的聊天消息
     */
    public List<ChatMessage> getChatMessagesRange(String chatId, LocalDate startDate, LocalDate endDate) {
        log.info("获取日期范围内的聊天消息: chatId={}, startDate={}, endDate={}", chatId, startDate, endDate);
        
        try {
            // 设置客户端 URL
            chatlogClient.setBaseUrl(chatlogConfigService.getChatlogConfig().getBaseUrl());
            
            List<ChatMessage> allMessages = chatlogClient.getChatMessagesRange(
                    chatId, 
                    startDate.format(DATE_FORMATTER), 
                    endDate.format(DATE_FORMATTER)
            );
            
            log.info("从 Chatlog服务获取到 {} 条消息（日期范围）", allMessages.size());
            
            return allMessages;
        } catch (Exception e) {
            log.error("从 Chatlog服务获取日期范围消息失败: chatId={}, startDate={}, endDate={}", 
                    chatId, startDate, endDate, e);
            throw new RuntimeException("获取日期范围内的聊天消息失败: " + e.getMessage());
        }
    }
}