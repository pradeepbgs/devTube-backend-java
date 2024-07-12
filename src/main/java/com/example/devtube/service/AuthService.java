package com.example.devtube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.devtube.lib.JwtTokenUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    public boolean isAuthenticated(HttpServletRequest request){
        final String jwtCookieName = "token"; 

        String token = null;
        Cookie [] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtCookieName)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // validate token
        if (token != null && jwtTokenUtil.validateToken(token)) {
            return true; // user is authenticated
        } 
        return false; // not authenticated
    }

    public String getUserFromRequest(HttpServletRequest request) {
        final String jwtCookieName = "token";

        String token = null;
        Cookie [] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtCookieName)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            return jwtTokenUtil.getUsernameFromToken(token);
        } else {
            return null;
        }
    }

}
