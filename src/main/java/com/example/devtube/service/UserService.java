/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.devtube.service;


import com.example.devtube.repository.userRepository;
import com.example.devtube.utils.FileUploader;
import com.example.devtube.utils.JwtTokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import com.example.devtube.entities.User;

/**
 *
 * @author pradeep
 */

@Service
public class UserService {
    @Autowired
    private userRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public Map<String, String> register(String username, String email, String password) {
        Map<String, String> response = new HashMap<>();

        if (username == null || username.isEmpty()) {
            response.put("error", "username cannot be empty");
            return response;
        }
        if (email == null || email.isEmpty()) {
            response.put("error", "email cannot be empty");
            return response;
        }
        if (password == null || password.isEmpty()) {
            response.put("error", "password cannot be empty");
            return response;
        }

        try {
            boolean existingUser = userRepository.existsByUsernameOrEmail(username, email);
            if (existingUser) {
                response.put("error", "Username or email already exists");
                return response;
            }

            User springUser = new User();
            springUser.setUsername(username);
            springUser.setEmail(email);
            springUser.setPassword(passwordEncoder.encode(password));

            userRepository.save(springUser);

            response.put("message", "user registered successfully");
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    public Map<String, String> login(String username, String password, HttpServletResponse response) {
        Map<String, String> responses = new HashMap<>();

        if (username.isEmpty() || username == null) {
            responses.put("err", "please provide username or email");
            return responses;
        }
        if (password.isEmpty() || password == null) {
            responses.put("err", "please provide password");
            return responses;
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            responses.put("error", "Invalid username");
            return responses;
        }

        boolean isPasswordValid = passwordEncoder.matches(password, user.getPassword());

        if (!isPasswordValid) {
            responses.put("err", "Invalid Credentials");
            return responses;
        }

        try {
            String token = jwtTokenUtil.generateToken(username, user.getId());
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            responses.put("message", "login successful");
            return responses;
        } catch (Exception e) {
            responses.put("error", e.getMessage());
            return responses;
        }
    }

    public Map<String, String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "logout successful");
        return responseMap;
    }

    public Map<String, Object> changeUserDetails(String username, String email, String password, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String loggedInUsername = (String) request.getAttribute("username"); // Assume username is set in request

            if (loggedInUsername == null) {
                response.put("error", "User not authenticated");
                return response;
            }

            User user = userRepository.findByUsername(loggedInUsername);

            if (username != null && !username.isEmpty()) {
                user.setUsername(username);
            }
            if (email != null && !email.isEmpty()) {
                user.setEmail(email);
            }
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userRepository.save(user);

            response.put("message", "User details updated");
            response.put("user", user);
            return response;
        } catch (Exception e) {
            response.put("error", "Failed to update user details");
            return response;
        }
    }
}
