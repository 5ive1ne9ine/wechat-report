package com.wechat.dailyreport.client;

import com.wechat.dailyreport.dto.request.AIAnalysisRequest;
import com.wechat.dailyreport.dto.response.AIAnalysisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * AI服务客户端
 */
@Component
@Slf4j
public class AIServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private String baseUrl = "https://api.openai.com";
    
    /**
     * 设置基础URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * 调用AI分析接口
     * 
     * @param authorization API密钥，格式为 "Bearer ${api_key}"
     * @param request AI分析请求
     * @return AI分析响应
     */
    public AIAnalysisResponse analyze(String authorization, AIAnalysisRequest request) {
        String url = baseUrl + "/v1/chat/completions";
        log.info("调用AI服务: {}", url);
        
        try {
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authorization);
            
            HttpEntity<AIAnalysisRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<AIAnalysisResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AIAnalysisResponse.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("调用AI服务失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用AI服务失败: " + e.getMessage());
        }
    }
}