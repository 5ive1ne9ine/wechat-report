package com.wechat.dailyreport.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception e) {
        log.error("发生异常: {}", e.getMessage(), e);
        
        ModelAndView mav = new ModelAndView();
        mav.addObject("error", e.getMessage());
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error");
        
        return mav;
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest request, RuntimeException e) {
        log.error("发生运行时异常: {}", e.getMessage(), e);
        
        ModelAndView mav = new ModelAndView();
        mav.addObject("error", e.getMessage());
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error");
        
        return mav;
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        log.error("发生参数异常: {}", e.getMessage(), e);
        
        ModelAndView mav = new ModelAndView();
        mav.addObject("error", "参数错误: " + e.getMessage());
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error");
        
        return mav;
    }
}