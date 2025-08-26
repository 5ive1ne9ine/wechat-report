package com.wechat.dailyreport.controller;

import com.wechat.dailyreport.service.ChatAnalysisService.AnalysisReport;
import com.wechat.dailyreport.service.ChatlogService.ChatSession;
import com.wechat.dailyreport.service.ChatAnalysisService;
import com.wechat.dailyreport.service.ChatlogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 聊天分析Controller
 */
@Controller
@RequestMapping("/chat-analysis")
@Slf4j
public class ChatAnalysisController {
    
    @Autowired
    private ChatAnalysisService chatAnalysisService;
    
    @Autowired
    private ChatlogService chatlogService;
    
    /**
     * 分析页面
     */
    @GetMapping({"", "/"})
    public String analysisPage(Model model) {
        log.info("访问聊天分析页面");
        
        try {
            List<ChatSession> chatSessions = chatlogService.getAllChatSessions();
            model.addAttribute("chatSessions", chatSessions);
            
            // 设置默认日期为今天
            model.addAttribute("defaultDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
        } catch (Exception e) {
            log.error("获取聊天会话列表失败", e);
            model.addAttribute("error", "获取聊天会话列表失败: " + e.getMessage());
        }
        
        return "analysis";
    }
    
    /**
     * 执行聊天分析
     */
    @PostMapping("/analyze")
    public String analyzeChat(@RequestParam("chatTarget") String chatTarget,
                             @RequestParam("analysisDate") String analysisDate,
                             RedirectAttributes redirectAttributes) {
        
        log.info("开始分析聊天: chatTarget={}, analysisDate={}", chatTarget, analysisDate);
        
        // 参数验证
        if (StringUtils.isBlank(chatTarget)) {
            redirectAttributes.addFlashAttribute("error", "请选择聊天对象");
            return "redirect:/chat-analysis/";
        }
        
        if (StringUtils.isBlank(analysisDate)) {
            redirectAttributes.addFlashAttribute("error", "请选择分析日期");
            return "redirect:/chat-analysis/";
        }
        
        try {
            LocalDate date = LocalDate.parse(analysisDate);
            
            // 执行分析
            AnalysisReport report = chatAnalysisService.analyzeChat(chatTarget, date);
            
            // 重定向到报告页面
            return "redirect:/chat-analysis/report/" + report.getReportId();
            
        } catch (Exception e) {
            log.error("聊天分析失败", e);
            redirectAttributes.addFlashAttribute("error", "分析失败: " + e.getMessage());
            return "redirect:/chat-analysis/";
        }
    }
    
    /**
     * 查看分析报告
     */
    @GetMapping("/report/{reportId}")
    public String viewReport(@PathVariable("reportId") String reportId, Model model) {
        log.info("查看分析报告: reportId={}", reportId);
        
        try {
            AnalysisReport report = chatAnalysisService.getAnalysisReport(reportId);
            
            if (report == null) {
                model.addAttribute("error", "报告不存在");
                return "error";
            }
            
            model.addAttribute("report", report);
            
            // 如果报告还在处理中，显示处理页面
            if ("PROCESSING".equals(report.getStatus())) {
                return "processing";
            }
            
            return "report";
            
        } catch (Exception e) {
            log.error("获取分析报告失败: reportId={}", reportId, e);
            model.addAttribute("error", "获取报告失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 分析历史记录
     */
    @GetMapping("/history")
    public String analysisHistory(@RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 Model model) {
        
        log.info("查看分析历史: page={}, size={}", page, size);
        
        try {
            Map<String, AnalysisReport> allReports = chatAnalysisService.getAnalysisHistory();
            List<AnalysisReport> reportList = new ArrayList<>(allReports.values());
            
            // 简单分页处理
            int total = reportList.size();
            int start = Math.max(0, (page - 1) * size);
            int end = Math.min(total, start + size);
            
            List<AnalysisReport> pageReports = start < total ? reportList.subList(start, end) : new ArrayList<>();
            
            model.addAttribute("reports", pageReports);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", (total + size - 1) / size);
            model.addAttribute("totalElements", total);
            
            return "history";
            
        } catch (Exception e) {
            log.error("获取分析历史失败", e);
            model.addAttribute("error", "获取历史记录失败: " + e.getMessage());
            return "error";
        }
    }
}