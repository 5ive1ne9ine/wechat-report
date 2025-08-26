package com.wechat.dailyreport.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 应用启动监听器
 */
@Component
@Slf4j
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        
        log.info("==========================================================");
        log.info("微信聊天分析器启动成功！");
        log.info("访问地址: http://localhost:{}{}", port, contextPath);
        log.info("==========================================================");
        
        // 检查关键配置
        checkConfiguration(env);
    }
    
    /**
     * 检查关键配置
     */
    private void checkConfiguration(Environment env) {
        // 检查数据库配置
        String datasourceUrl = env.getProperty("spring.datasource.url");
        if (datasourceUrl == null || datasourceUrl.isEmpty()) {
            log.warn("数据库配置未设置，请检查application.yml中的spring.datasource配置");
        }
        
        // 检查Redis配置
        String redisHost = env.getProperty("spring.redis.host");
        if (redisHost == null || redisHost.isEmpty()) {
            log.warn("Redis配置未设置，请检查application.yml中的spring.redis配置");
        }
        
        // 检查AI服务配置
        String aiBaseUrl = env.getProperty("app.ai.default.base-url");
        if (aiBaseUrl == null || aiBaseUrl.isEmpty()) {
            log.warn("AI服务配置未设置，请在系统配置页面中设置AI服务参数");
        }
        
        // 检查Chatlog服务配置
        String chatlogBaseUrl = env.getProperty("app.chatlog.default.base-url");
        if (chatlogBaseUrl == null || chatlogBaseUrl.isEmpty()) {
            log.warn("Chatlog服务配置未设置，请在系统配置页面中设置Chatlog服务参数");
        }
        
        log.info("配置检查完成");
    }
}