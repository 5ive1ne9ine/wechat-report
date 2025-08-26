package com.wechat.dailyreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 微信聊天分析器应用主启动类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@SpringBootApplication
@EnableCaching
public class WechatDailyReportApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WechatDailyReportApplication.class, args);
        System.out.println("微信聊天可视化分析系统启动成功!");
    }
}