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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

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
//            List<ChatSession> chatSessions = chatlogService.getAllChatSessions();
//            model.addAttribute("chatSessions", chatSessions);
//
//            // 获取私聊会话列表
//            List<ChatSession> privateChatSessions = chatlogService.getPrivateChatSessions();
//            model.addAttribute("privateChatSessions", privateChatSessions);
            
            // 设置默认日期范围为最近一周
            LocalDate today = LocalDate.now();
            LocalDate oneWeekAgo = today.minusDays(7);
            model.addAttribute("defaultStartDate", oneWeekAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("defaultEndDate", today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
        } catch (Exception e) {
            log.error("获取聊天会话列表失败", e);
            model.addAttribute("error", "获取聊天会话列表失败: " + e.getMessage());
        }
        
        return "analysis";
    }
    
    /**
     * 根据群聊名称搜索群聊会话
     */
    @GetMapping("/search-groups")
    @ResponseBody
    public ResponseEntity<List<ChatSession>> searchGroupChats(@RequestParam("name") String groupName) {
        log.info("搜索群聊: groupName={}", groupName);
        
        try {
            List<ChatSession> groups = chatlogService.searchGroupChatsByName(groupName);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            log.error("搜索群聊失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(Collections.emptyList());
        }
    }
    
    /**
     * 执行聊天分析 - 支持群聊和私聊，支持日期范围
     */
    @PostMapping("/analyze")
    public String analyzeChat(@RequestParam("analysisType") String analysisType,
                             @RequestParam(value = "niceName", required = false) String niceName,
                             @RequestParam(value = "privateChatId", required = false) String privateChatId,
                             @RequestParam("startDate") String startDate,
                             @RequestParam("endDate") String endDate,
                             RedirectAttributes redirectAttributes) {
        
        log.info("开始分析聊天: type={}, groupId={}, privateId={}, startDate={}, endDate={}", 
                analysisType, niceName, privateChatId, startDate, endDate);
        
        String chatTarget = null;
        
        // 根据分析类型确定聊天目标
        if ("group".equals(analysisType)) {
            if (StringUtils.isBlank(niceName)) {
                redirectAttributes.addFlashAttribute("error", "请选择群聊");
                return "redirect:/chat-analysis/";
            }
            chatTarget = niceName;
        } else if ("private".equals(analysisType)) {
            if (StringUtils.isBlank(privateChatId)) {
                redirectAttributes.addFlashAttribute("error", "请选择私聊对象");
                return "redirect:/chat-analysis/";
            }
            chatTarget = privateChatId;
        } else {
            redirectAttributes.addFlashAttribute("error", "请选择分析类型");
            return "redirect:/chat-analysis/";
        }
        
        // 验证日期范围
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            if (start.isAfter(end)) {
                redirectAttributes.addFlashAttribute("error", "开始日期不能晚于结束日期");
                return "redirect:/chat-analysis/";
            }
            
            // 执行分析逻辑
            AnalysisReport report = chatAnalysisService.analyzeChatRange(chatTarget, start, end);
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