package com.wechat.dailyreport.client;

import com.wechat.dailyreport.service.ChatlogService.ChatMessage;
import com.wechat.dailyreport.service.ChatlogService.ChatSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Chatlog服务客户端
 */
@Component
@Slf4j
public class ChatlogClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private String baseUrl = "http://127.0.0.1:5030";
    
    /**
     * 设置基础URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * 获取所有聊天会话
     */
    public List<ChatSession> getChatSessions() {
        String url = baseUrl + "/api/v1/chatroom?format=json";
        log.info("调用Chatlog服务获取聊天会话: {}", url);
        
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null ,
                    new ParameterizedTypeReference<Object>() {}
            );
            
            return (List<ChatSession>) response.getBody();
        } catch (Exception e) {
            log.error("获取聊天会话失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取聊天会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定聊天的消息记录
     * 
     * @param chatId 聊天ID
     * @param date 日期，格式为 yyyy-MM-dd
     * @return 聊天消息列表
     */
    public List<ChatMessage> getChatMessages(String chatId, String date) {
        String url = baseUrl + "/api/chats/" + chatId + "/messages?date=" + date;
        log.info("调用Chatlog服务获取聊天消息: {}", url);
        
        try {
            ResponseEntity<List<ChatMessage>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ChatMessage>>() {}
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("获取聊天消息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取聊天消息失败: " + e.getMessage());
        }
    }
}