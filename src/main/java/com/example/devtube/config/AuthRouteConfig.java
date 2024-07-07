package com.example.devtube.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.devtube.interceptors.AuthInterceptor;

@Configuration
public class AuthRouteConfig implements WebMvcConfigurer{
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
        .addPathPatterns("/api/user/**")
        .excludePathPatterns("/api/user/register","/api/user/login");
    }
}
