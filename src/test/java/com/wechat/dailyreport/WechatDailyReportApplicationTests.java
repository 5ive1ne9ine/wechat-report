package com.wechat.dailyreport;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 应用集成测试
 */
@SpringBootTest
@ActiveProfiles("test")
class WechatDailyReportApplicationTests {
    
    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
    }
}