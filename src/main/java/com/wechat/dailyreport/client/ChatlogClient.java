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
import java.util.Map;

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
     * 获取所有群聊会话，支持按名称搜索
     * @param niceName 群聊名称，可为null或空字符串，为空时返回所有群聊
     */
    public List<ChatSession> getGroupChatSessionsByNiceName(String niceName) {
        // 处理空字符串或null参数
        String keyword = (niceName == null || niceName.trim().isEmpty()) ? "" : niceName.trim();
        String url = baseUrl + String.format("/api/v1/chatroom?format=json&keyword=%s", keyword);
        log.info("调用Chatlog服务获取群聊会话: {}, keyword: {}", url, keyword);
        
        try {
            ResponseEntity<Map<String, List<ChatSession>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null ,
                    new ParameterizedTypeReference<Map<String, List<ChatSession>>>() {}
            );
            return response.getBody().get("items");
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
    
    /**
     * 获取指定日期范围内的聊天消息记录
     * 
     * @param chatId 聊天ID
     * @param startDate 开始日期，格式为 yyyy-MM-dd
     * @param endDate 结束日期，格式为 yyyy-MM-dd
     * @return 聊天消息列表
     */
    public List<ChatMessage> getChatMessagesRange(String chatId, String startDate, String endDate) {
        String url = baseUrl + "/api/chats/" + chatId + "/messages?start_date=" + startDate + "&end_date=" + endDate;
        log.info("调用Chatlog服务获取日期范围消息: {}", url);
        
        try {
            ResponseEntity<List<ChatMessage>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ChatMessage>>() {}
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("获取日期范围消息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取日期范围消息失败: " + e.getMessage());
        }
    }
}