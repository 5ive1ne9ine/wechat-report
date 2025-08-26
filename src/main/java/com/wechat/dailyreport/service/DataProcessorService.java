package com.wechat.dailyreport.service;

import com.wechat.dailyreport.service.ChatlogService.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据处理服务
 */
@Service
@Slf4j
public class DataProcessorService {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * 处理聊天消息，转换为AI分析所需的格式
     * 
     * @param messages 聊天消息列表
     * @return 格式化后的聊天数据
     */
    public String processMessages(List<ChatMessage> messages) {
        log.info("开始处理聊天消息，消息数量: {}", messages.size());
        
        String processedData = messages.stream()
                .filter(this::isValidMessage)
                .map(this::formatMessage)
                .collect(Collectors.joining("\n"));
        
        log.info("聊天消息处理完成，有效消息数量: {}", 
                processedData.split("\n").length);
        
        return processedData;
    }
    
    /**
     * 验证消息是否有效
     */
    private boolean isValidMessage(ChatMessage message) {
        return message != null &&
               StringUtils.isNotBlank(message.getContent()) &&
               "TEXT".equals(message.getMessageType()) &&
               message.getTimestamp() != null;
    }
    
    /**
     * 格式化单条消息
     */
    private String formatMessage(ChatMessage message) {
        String timeStr = message.getTimestamp().format(FORMATTER);
        String senderName = StringUtils.isNotBlank(message.getSenderName()) ? 
                           message.getSenderName() : "未知用户";
        
        return String.format("[%s] %s: %s", timeStr, senderName, message.getContent().trim());
    }
    
    /**
     * 统计消息基本信息
     */
    public String generateMessageStatistics(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "本日无有效聊天记录";
        }
        
        long validMessages = messages.stream()
                .filter(this::isValidMessage)
                .count();
        
        long participantCount = messages.stream()
                .filter(this::isValidMessage)
                .map(ChatMessage::getSenderId)
                .distinct()
                .count();
        
        return String.format("统计信息：总消息数 %d 条，有效消息 %d 条，参与人数 %d 人",
                messages.size(), validMessages, participantCount);
    }
}