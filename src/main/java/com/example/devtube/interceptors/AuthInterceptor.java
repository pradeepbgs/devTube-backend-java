package com.example.devtube.interceptors;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.devtube.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor{
    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, 
    HttpServletResponse response, Object handler) throws IOException {
        if (authService.isAuthenticated(request)) {
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            response.getWriter().flush();
            return false;
        }
    }
}
