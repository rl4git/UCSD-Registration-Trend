package com.ucsdregistration.web_backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 这条规则适用于你所有 /api/ 开头的路径
            .allowedOrigins("https://www.ucsdregistration.com") // **最关键的一步：明确允许你的前端源**
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
            .allowedHeaders("*") // 允许所有的请求头
            .allowCredentials(true); // 是否允许发送Cookie
    }
}