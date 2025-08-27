package com.wechat.dailyreport.controller;

import com.wechat.dailyreport.dto.config.AIServiceConfig;
import com.wechat.dailyreport.dto.config.ChatlogServiceConfig;
import com.wechat.dailyreport.service.AIConfigService;
import com.wechat.dailyreport.service.ChatlogConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 配置管理Controller
 */
@Controller
@RequestMapping("/config")
@Slf4j
public class ConfigController {

    @Autowired
    private AIConfigService aiConfigService;

    @Autowired
    private ChatlogConfigService chatlogConfigService;

    /**
     * 配置页面
     */
    @GetMapping({"", "/"})
    public String configPage(Model model) {
        log.info("访问配置管理页面");

        try {
            AIServiceConfig aiConfig = aiConfigService.getAIConfig();
            ChatlogServiceConfig chatlogConfig = chatlogConfigService.getChatlogConfig();

            model.addAttribute("aiConfig", aiConfig);
            model.addAttribute("chatlogConfig", chatlogConfig);

        } catch (Exception e) {
            log.error("获取配置信息失败", e);
            model.addAttribute("error", "获取配置信息失败: " + e.getMessage());
        }

        return "config";
    }

    /**
     * 更新AI服务配置
     */
    @PostMapping("/ai-service")
    public String updateAIConfig(@ModelAttribute AIServiceConfig config,
                                 RedirectAttributes redirectAttributes) {

        log.info("更新AI服务配置: {}", config);

        try {
            aiConfigService.updateAIConfig(config);
            redirectAttributes.addFlashAttribute("success", "AI服务配置更新成功");

        } catch (Exception e) {
            log.error("更新AI服务配置失败", e);
            redirectAttributes.addFlashAttribute("error", "更新AI服务配置失败: " + e.getMessage());
        }

        return "redirect:/config/";
    }

    /**
     * 更新Chatlog服务配置
     */
    @PostMapping("/chatlog-service")
    public String updateChatlogConfig(@ModelAttribute ChatlogServiceConfig config,
                                      RedirectAttributes redirectAttributes) {

        log.info("更新Chatlog服务配置: {}", config);

        try {
            chatlogConfigService.updateChatlogConfig(config);
            redirectAttributes.addFlashAttribute("success", "Chatlog服务配置更新成功");

        } catch (Exception e) {
            log.error("更新Chatlog服务配置失败", e);
            redirectAttributes.addFlashAttribute("error", "更新Chatlog服务配置失败: " + e.getMessage());
        }

        return "redirect:/config/";
    }
}