package com.example.devtube.service;

import com.example.devtube.utils.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  private static final String JWT_COOKIE_NAME = "token";

  public boolean isAuthenticated(HttpServletRequest request) {
    String token = extractTokenFromCookies(request);
    // validate token
    return token != null && jwtTokenUtil.validateToken(token);
  }

  public String getUserFromRequest(HttpServletRequest request) {
    String token = extractTokenFromCookies(request);

    if (token != null) {
      return jwtTokenUtil.getUsernameFromToken(token);
    } else {
      return null;
    }
  }

  public String extractTokenFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (JWT_COOKIE_NAME.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
