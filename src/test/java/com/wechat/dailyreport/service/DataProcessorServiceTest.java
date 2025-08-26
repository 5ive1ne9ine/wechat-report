package com.wechat.dailyreport.service;

import com.wechat.dailyreport.service.ChatlogService.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DataProcessorService 单元测试
 */
class DataProcessorServiceTest {
    
    private DataProcessorService dataProcessorService;
    
    @BeforeEach
    void setUp() {
        dataProcessorService = new DataProcessorService();
    }
    
    @Test
    void testProcessMessages_ValidMessages() {
        // 准备测试数据
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage()
                        .setMessageId("msg1")
                        .setSenderId("user1")
                        .setSenderName("张三")
                        .setContent("你好")
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.of(2024, 1, 1, 10, 30, 0)),
                new ChatMessage()
                        .setMessageId("msg2")
                        .setSenderId("user2")
                        .setSenderName("李四")
                        .setContent("你好，很高兴认识你")
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.of(2024, 1, 1, 10, 31, 0))
        );
        
        // 执行测试
        String result = dataProcessorService.processMessages(messages);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("[10:30:00] 张三: 你好"));
        assertTrue(result.contains("[10:31:00] 李四: 你好，很高兴认识你"));
        
        String[] lines = result.split("\n");
        assertEquals(2, lines.length);
    }
    
    @Test
    void testProcessMessages_FilterInvalidMessages() {
        // 准备测试数据，包含无效消息
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage()
                        .setMessageId("msg1")
                        .setSenderId("user1")
                        .setSenderName("张三")
                        .setContent("你好")
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.of(2024, 1, 1, 10, 30, 0)),
                new ChatMessage()
                        .setMessageId("msg2")
                        .setSenderId("user2")
                        .setSenderName("李四")
                        .setContent("") // 空内容
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.of(2024, 1, 1, 10, 31, 0)),
                new ChatMessage()
                        .setMessageId("msg3")
                        .setSenderId("user3")
                        .setSenderName("王五")
                        .setContent("图片消息")
                        .setMessageType("IMAGE") // 非文本消息
                        .setTimestamp(LocalDateTime.of(2024, 1, 1, 10, 32, 0)),
                new ChatMessage()
                        .setMessageId("msg4")
                        .setSenderId("user4")
                        .setSenderName("赵六")
                        .setContent("   ") // 只有空格
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.of(2024, 1, 1, 10, 33, 0))
        );
        
        // 执行测试
        String result = dataProcessorService.processMessages(messages);
        
        // 验证结果 - 只有第一条消息应该被保留
        assertNotNull(result);
        assertTrue(result.contains("[10:30:00] 张三: 你好"));
        assertFalse(result.contains("李四"));
        assertFalse(result.contains("王五"));
        assertFalse(result.contains("赵六"));
        
        String[] lines = result.split("\n");
        assertEquals(1, lines.length);
    }
    
    @Test
    void testProcessMessages_EmptyList() {
        // 执行测试
        String result = dataProcessorService.processMessages(Arrays.asList());
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testGenerateMessageStatistics() {
        // 准备测试数据
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage()
                        .setMessageId("msg1")
                        .setSenderId("user1")
                        .setSenderName("张三")
                        .setContent("你好")
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.now()),
                new ChatMessage()
                        .setMessageId("msg2")
                        .setSenderId("user2")
                        .setSenderName("李四")
                        .setContent("你好")
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.now()),
                new ChatMessage()
                        .setMessageId("msg3")
                        .setSenderId("user1")
                        .setSenderName("张三")
                        .setContent("再见")
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.now())
        );
        
        // 执行测试
        String result = dataProcessorService.generateMessageStatistics(messages);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("总消息数 3 条"));
        assertTrue(result.contains("有效消息 3 条"));
        assertTrue(result.contains("参与人数 2 人"));
    }
    
    @Test
    void testGenerateMessageStatistics_EmptyList() {
        // 执行测试
        String result = dataProcessorService.generateMessageStatistics(Arrays.asList());
        
        // 验证结果
        assertNotNull(result);
        assertEquals("本日无有效聊天记录", result);
    }
    
    @Test
    void testGenerateMessageStatistics_NullList() {
        // 执行测试
        String result = dataProcessorService.generateMessageStatistics(null);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("本日无有效聊天记录", result);
    }
}