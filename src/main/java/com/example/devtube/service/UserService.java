package com.example.devtube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.devtube.dto.UserInfoDTO;
import com.example.devtube.entities.User;
import com.example.devtube.repository.UserRepository;
import com.example.devtube.utils.ApiResponse;
import com.example.devtube.utils.JwtTokenUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public ApiResponse register(UserInfoDTO userInfoDTO) {
        if (userInfoDTO.getUsername() == null || userInfoDTO.getUsername().isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Username cannot be empty", null);
        }
        
        if (userInfoDTO.getEmail() == null || userInfoDTO.getEmail().isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Email cannot be empty", null);
        }
        if (userInfoDTO.getPassword() == null || userInfoDTO.getPassword().isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Password cannot be empty", null);
        }

        try {
            boolean existingUser = userRepository.existsByUsernameOrEmail(userInfoDTO.getUsername(), userInfoDTO.getEmail());
            if (existingUser) {
                return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Username or email already exists", null);
            }

            User springUser = new User();
            springUser.setUsername(userInfoDTO.getUsername());
            springUser.setEmail(userInfoDTO.getEmail());
            springUser.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));

            userRepository.save(springUser);

            return new ApiResponse(HttpStatus.CREATED.value(), "User registered successfully", springUser);
        } catch (Exception e) {
            return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ApiResponse login(UserInfoDTO userInfo, HttpServletResponse response) {
        if (userInfo.getUsername() == null || userInfo.getUsername().isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Please provide username or email", null);
        }
        if (userInfo.getPassword() == null || userInfo.getPassword().isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Please provide password", null);
        }

        User user = userRepository.findByUsername(userInfo.getUsername());
        if (user == null) {
            return new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid username", null);
        }

        boolean isPasswordValid = passwordEncoder.matches(userInfo.getPassword(), user.getPassword());
        if (!isPasswordValid) {
            return new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", null);
        }

        try {
            String token = jwtTokenUtil.generateToken(userInfo.getUsername(), user.getId());
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return new ApiResponse(HttpStatus.OK.value(), "Login successful", null);
        } catch (Exception e) {
            return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ApiResponse logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return new ApiResponse(HttpStatus.OK.value(), "Logout successful", null);
    }

    public ApiResponse changeUserDetails(UserInfoDTO userInfo, HttpServletRequest request) {
        try {
            String loggedInUsername = (String) request.getAttribute("username"); // Assume username is set in request

            if (loggedInUsername == null) {
                return new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "User not authenticated", null);
            }

            User user = userRepository.findByUsername(loggedInUsername);

            if (userInfo.getUsername() != null && !userInfo.getUsername().isEmpty()) {
                user.setUsername(userInfo.getUsername());
            }
            if (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()) {
                user.setEmail(userInfo.getEmail());
            }
            if (userInfo.getPassword() != null && !userInfo.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userInfo.getPassword()));
            }

            userRepository.save(user);

            return new ApiResponse(HttpStatus.OK.value(), "User details updated", user);
        } catch (Exception e) {
            return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update user details", null);
        }
    }
}
