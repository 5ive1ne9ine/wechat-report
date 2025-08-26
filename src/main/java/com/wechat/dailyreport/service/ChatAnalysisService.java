package com.wechat.dailyreport.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天分析服务
 */
@Service
@Slf4j
public class ChatAnalysisService {
    
    @Autowired
    private ChatlogService chatlogService;
    
    @Autowired
    private DataProcessorService dataProcessorService;
    
    @Autowired
    private AIService aiService;
    
    // 使用内存存储分析报告（生产环境应该使用数据库）
    private final Map<String, AnalysisReport> reportStorage = new ConcurrentHashMap<>();
    
    /**
     * 分析报告数据类
     */
    public static class AnalysisReport {
        private String reportId;
        private String chatId;
        private String chatName;
        private LocalDate analysisDate;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private String rawData;
        private String structuredData;
        private String finalReport;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;

        // Getters and Setters
        public String getReportId() { return reportId; }
        public AnalysisReport setReportId(String reportId) { this.reportId = reportId; return this; }

        public String getChatId() { return chatId; }
        public AnalysisReport setChatId(String chatId) { this.chatId = chatId; return this; }

        public String getChatName() { return chatName; }
        public AnalysisReport setChatName(String chatName) { this.chatName = chatName; return this; }

        public LocalDate getAnalysisDate() { return analysisDate; }
        public AnalysisReport setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; return this; }

        public LocalDate getStartDate() { return startDate; }
        public AnalysisReport setStartDate(LocalDate startDate) { this.startDate = startDate; return this; }

        public LocalDate getEndDate() { return endDate; }
        public AnalysisReport setEndDate(LocalDate endDate) { this.endDate = endDate; return this; }

        public String getStatus() { return status; }
        public AnalysisReport setStatus(String status) { this.status = status; return this; }

        public String getRawData() { return rawData; }
        public AnalysisReport setRawData(String rawData) { this.rawData = rawData; return this; }

        public String getStructuredData() { return structuredData; }
        public AnalysisReport setStructuredData(String structuredData) { this.structuredData = structuredData; return this; }

        public String getFinalReport() { return finalReport; }
        public AnalysisReport setFinalReport(String finalReport) { this.finalReport = finalReport; return this; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public AnalysisReport setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public LocalDateTime getCompletedAt() { return completedAt; }
        public AnalysisReport setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
    }
    
    /**
     * 分析聊天数据
     * 
     * @param niceName 聊天ID
     * @param analysisDate 分析日期
     * @return 分析报告
     */
    public AnalysisReport analyzeChat(String niceName, LocalDate analysisDate) {
        log.info("开始分析聊天数据: niceName={}, date={}", niceName, analysisDate);
        
        // 检查是否已存在相同的分析报告
        AnalysisReport existingReport = getExistingReport(niceName, analysisDate);
        if (existingReport != null && "COMPLETED".equals(existingReport.getStatus())) {
            log.info("发现已存在的分析报告: {}", existingReport.getReportId());
            return existingReport;
        }

        // 获取聊天会话信息
        ChatlogService.ChatSession chatSession = chatlogService.getChatSessionByNiceName(niceName);
        if (chatSession == null) {
            throw new RuntimeException("未找到聊天会话: " + niceName);
        }
        
        // 创建新的分析报告记录
        AnalysisReport report = createAnalysisReport(niceName, chatSession.getNickName(), analysisDate);
        
        try {
            // 1. 获取聊天数据
            log.info("步骤1: 获取聊天数据");
            List<ChatlogService.ChatMessage> messages = chatlogService.getChatMessages(niceName, analysisDate);
            
            if (messages.isEmpty()) {
                updateReportAsFailed(report, "指定日期无聊天数据");
                throw new RuntimeException("指定日期无聊天数据");
            }
            
            // 2. 数据预处理
            log.info("步骤2: 数据预处理");
            String processedData = dataProcessorService.processMessages(messages);
            String statistics = dataProcessorService.generateMessageStatistics(messages);
            String rawData = statistics + "\n\n" + processedData;
            
            // 更新原始数据
            report.setRawData(rawData);
            reportStorage.put(report.getReportId(), report);
            
            // 3. AI结构化分析
            log.info("步骤3: AI结构化分析");
            String structuredData = aiService.structureAnalysis(processedData);
            
            // 更新结构化数据
            report.setStructuredData(structuredData);
            reportStorage.put(report.getReportId(), report);
            
            // 4. AI生成最终报告
            log.info("步骤4: AI生成最终报告");
            String finalReport = aiService.generateReport(structuredData);
            
            // 5. 保存最终结果
            log.info("步骤5: 保存分析结果");
            report.setFinalReport(finalReport)
                  .setStatus("COMPLETED")
                  .setCompletedAt(LocalDateTime.now());
            
            reportStorage.put(report.getReportId(), report);
            
            log.info("聊天分析完成: reportId={}", report.getReportId());
            return report;
            
        } catch (Exception e) {
            log.error("聊天分析失败: reportId={}", report.getReportId(), e);
            updateReportAsFailed(report, e.getMessage());
            throw new RuntimeException("聊天分析失败: " + e.getMessage());
        }
    }
    public AnalysisReport analyzeChatRange(String niceName, String startDate, String endDate) {
        log.info("开始分析日期范围聊天数据: niceName={}, startDate={}, endDate={}", niceName, startDate, endDate);
        
        // 检查是否已存在相同的分析报告
        AnalysisReport existingReport = getExistingRangeReport(niceName, startDate, startDate);
        if (existingReport != null && "COMPLETED".equals(existingReport.getStatus())) {
            log.info("发现已存在的分析报告: {}", existingReport.getReportId());
            return existingReport;
        }

        // 获取聊天会话信息
        ChatlogService.ChatSession chatSession = chatlogService.getChatSessionByNiceName(niceName);
        if (chatSession == null) {
            throw new RuntimeException("未找到聊天会话: " + niceName);
        }
        
        // 创建新的分析报告记录
        AnalysisReport report = createAnalysisReportRange(niceName, chatSession.getNickName(), LocalDate.parse(startDate), LocalDate.parse(endDate));
        
        try {
            // 1. 获取日期范围内的聊天数据
            log.info("步骤1: 获取日期范围内的聊天数据");
            List<ChatlogService.ChatMessage> allMessages = chatlogService.getChatMessagesRange(niceName, startDate, endDate);
            
            if (allMessages.isEmpty()) {
                updateReportAsFailed(report, "指定日期范围无聊天数据");
                throw new RuntimeException("指定日期范围无聊天数据");
            }
            
            // 2. 数据预处理
            log.info("步骤2: 数据预处理");
            String processedData = dataProcessorService.processMessages(allMessages);
            String statistics = dataProcessorService.generateMessageStatistics(allMessages);
            String rawData = statistics + "\n\n" + processedData;
            
            // 更新原始数据
            report.setRawData(rawData);
            reportStorage.put(report.getReportId(), report);
            
            // 3. AI结构化分析
            log.info("步骤3: AI结构化分析");
            String structuredData = aiService.structureAnalysis(processedData);
            
            // 更新结构化数据
            report.setStructuredData(structuredData);
            reportStorage.put(report.getReportId(), report);
            
            // 4. AI生成最终报告
            log.info("步骤4: AI生成最终报告");
            String finalReport = aiService.generateReport(structuredData);
            
            // 5. 保存最终结果
            log.info("步骤5: 保存分析结果");
            report.setFinalReport(finalReport)
                  .setStatus("COMPLETED")
                  .setCompletedAt(LocalDateTime.now());
            
            reportStorage.put(report.getReportId(), report);
            
            log.info("日期范围聊天分析完成: reportId={}", report.getReportId());
            return report;
            
        } catch (Exception e) {
            log.error("日期范围聊天分析失败: reportId={}", report.getReportId(), e);
            updateReportAsFailed(report, e.getMessage());
            throw new RuntimeException("聊天分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取分析报告
     */
    public AnalysisReport getAnalysisReport(String reportId) {
        return reportStorage.get(reportId);
    }
    
    /**
     * 获取分析历史记录
     */
    public Map<String, AnalysisReport> getAnalysisHistory() {
        return new HashMap<>(reportStorage);
    }
    
    /**
     * 检查是否存在相同的分析报告
     */
    private AnalysisReport getExistingReport(String chatId, LocalDate analysisDate) {
        return reportStorage.values().stream()
                .filter(report -> chatId.equals(report.getChatId()) && 
                                 analysisDate.equals(report.getAnalysisDate()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 检查是否存在相同的日期范围分析报告
     */
    private AnalysisReport getExistingRangeReport(String niceName, String startDate, String endDate) {
        return reportStorage.values().stream()
                .filter(report -> niceName.equals(report.getChatId()) &&
                                 startDate.equals(report.getStartDate()) &&
                                 endDate.equals(report.getEndDate()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 创建分析报告记录
     */
    private AnalysisReport createAnalysisReport(String chatId, String chatName, LocalDate analysisDate) {
        String reportId = UUID.randomUUID().toString().replace("-", "");
        
        AnalysisReport report = new AnalysisReport()
                .setReportId(reportId)
                .setChatId(chatId)
                .setChatName(chatName)
                .setAnalysisDate(analysisDate)
                .setStatus("PROCESSING")
                .setCreatedAt(LocalDateTime.now());
        
        reportStorage.put(reportId, report);
        log.info("创建分析报告记录: reportId={}", reportId);
        
        return report;
    }
    
    /**
     * 创建日期范围分析报告记录
     */
    private AnalysisReport createAnalysisReportRange(String chatId, String chatName, LocalDate startDate, LocalDate endDate) {
        String reportId = UUID.randomUUID().toString().replace("-", "");
        
        AnalysisReport report = new AnalysisReport()
                .setReportId(reportId)
                .setChatId(chatId)
                .setChatName(chatName)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus("PROCESSING")
                .setCreatedAt(LocalDateTime.now());
        
        reportStorage.put(reportId, report);
        log.info("创建日期范围分析报告记录: reportId={}, startDate={}, endDate={}", reportId, startDate, endDate);
        
        return report;
    }
    
    /**
     * 更新报告为失败状态
     */
    private void updateReportAsFailed(AnalysisReport report, String errorMessage) {
        try {
            report.setStatus("FAILED")
                  .setFinalReport("分析失败: " + errorMessage)
                  .setCompletedAt(LocalDateTime.now());
            
            reportStorage.put(report.getReportId(), report);
        } catch (Exception e) {
            log.error("更新报告失败状态时出错", e);
        }
    }
}