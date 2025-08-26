package com.wechat.dailyreport.service;

import com.wechat.dailyreport.service.ChatAnalysisService.AnalysisReport;
import com.wechat.dailyreport.service.ChatlogService.ChatMessage;
import com.wechat.dailyreport.service.ChatlogService.ChatSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ChatAnalysisService 单元测试
 */
class ChatAnalysisServiceTest {
    
    @Mock
    private ChatlogService chatlogService;
    
    @Mock
    private DataProcessorService dataProcessorService;
    
    @Mock
    private AIService aiService;
    
    @InjectMocks
    private ChatAnalysisService chatAnalysisService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testAnalyzeChat_Success() {
        // 准备测试数据
        String chatId = "test-chat-id";
        LocalDate analysisDate = LocalDate.now();
        
        ChatSession chatSession = new ChatSession()
                .setChatId(chatId)
                .setChatName("测试聊天");
        
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage()
                        .setMessageId("msg1")
                        .setChatId(chatId)
                        .setSenderId("user1")
                        .setSenderName("用户1")
                        .setContent("你好")
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.now()),
                new ChatMessage()
                        .setMessageId("msg2")
                        .setChatId(chatId)
                        .setSenderId("user2")
                        .setSenderName("用户2")
                        .setContent("你好，很高兴认识你")
                        .setMessageType("TEXT")
                        .setTimestamp(LocalDateTime.now())
        );
        
        String processedData = "[10:30:00] 用户1: 你好\n[10:31:00] 用户2: 你好，很高兴认识你";
        String structuredData = "{\"summary\":{\"total_messages\":2,\"participants\":[\"用户1\",\"用户2\"]}}";
        String finalReport = "<h2>聊天分析报告</h2><p>这是一次友好的对话...</p>";
        
        // Mock 行为
        when(chatlogService.getChatSessionById(chatId)).thenReturn(chatSession);
        when(chatlogService.getChatMessages(chatId, analysisDate)).thenReturn(messages);
        when(dataProcessorService.processMessages(messages)).thenReturn(processedData);
        when(dataProcessorService.generateMessageStatistics(messages)).thenReturn("统计信息：总消息数 2 条，有效消息 2 条，参与人数 2 人");
        when(aiService.structureAnalysis(anyString())).thenReturn(structuredData);
        when(aiService.generateReport(structuredData)).thenReturn(finalReport);
        
        // 执行测试
        AnalysisReport result = chatAnalysisService.analyzeChat(chatId, analysisDate);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getReportId());
        assertEquals(chatId, result.getChatId());
        assertEquals("测试聊天", result.getChatName());
        assertEquals(analysisDate, result.getAnalysisDate());
        assertEquals("COMPLETED", result.getStatus());
        assertEquals(finalReport, result.getFinalReport());
        
        // 验证方法调用
        verify(chatlogService).getChatSessionById(chatId);
        verify(chatlogService).getChatMessages(chatId, analysisDate);
        verify(dataProcessorService).processMessages(messages);
        verify(aiService).structureAnalysis(anyString());
        verify(aiService).generateReport(structuredData);
    }
    
    @Test
    void testAnalyzeChat_NoMessages() {
        // 准备测试数据
        String chatId = "test-chat-id";
        LocalDate analysisDate = LocalDate.now();
        
        ChatSession chatSession = new ChatSession()
                .setChatId(chatId)
                .setChatName("测试聊天");
        
        List<ChatMessage> emptyMessages = Arrays.asList();
        
        // Mock 行为
        when(chatlogService.getChatSessionById(chatId)).thenReturn(chatSession);
        when(chatlogService.getChatMessages(chatId, analysisDate)).thenReturn(emptyMessages);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            chatAnalysisService.analyzeChat(chatId, analysisDate);
        });
        
        assertTrue(exception.getMessage().contains("指定日期无聊天数据"));
        
        // 验证方法调用
        verify(chatlogService).getChatSessionById(chatId);
        verify(chatlogService).getChatMessages(chatId, analysisDate);
    }
    
    @Test
    void testAnalyzeChat_ChatSessionNotFound() {
        // 准备测试数据
        String chatId = "nonexistent-chat-id";
        LocalDate analysisDate = LocalDate.now();
        
        // Mock 行为
        when(chatlogService.getChatSessionById(chatId)).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            chatAnalysisService.analyzeChat(chatId, analysisDate);
        });
        
        assertTrue(exception.getMessage().contains("未找到聊天会话"));
        
        // 验证方法调用
        verify(chatlogService).getChatSessionById(chatId);
        verify(chatlogService, never()).getChatMessages(anyString(), any(LocalDate.class));
    }
}