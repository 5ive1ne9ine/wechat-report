package com.wechat.dailyreport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页Controller
 */
@Controller
public class HomeController {
    
    /**
     * 首页重定向到聊天分析页面
     */
    @GetMapping({"/", "/index"})
    public String home() {
        return "redirect:/chat-analysis/";
    }
}