package com.example.devtube.service;

import com.example.devtube.dto.UserInfoDTO;
import com.example.devtube.entities.User;
import com.example.devtube.repository.userRepository;
import com.example.devtube.utils.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private userRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;


  public Map<String, String> register(UserInfoDTO userInfoDTO) {
    Map<String, String> response = new HashMap<>();

    if (userInfoDTO.getUsername() == null || userInfoDTO.getUsername().isEmpty()) {
      response.put("error", "username cannot be empty");
      return response;
    }
    if (userInfoDTO.getEmail() == null || userInfoDTO.getEmail().isEmpty()) {
      response.put("error", "email cannot be empty");
      return response;
    }
    if (userInfoDTO.getPassword() == null || userInfoDTO.getPassword().isEmpty()) {
      response.put("error", "password cannot be empty");
      return response;
    }

    try {
      boolean existingUser = userRepository.existsByUsernameOrEmail(userInfoDTO.getUsername(), userInfoDTO.getEmail());
      if (existingUser) {
        response.put("error", "Username or email already exists");
        return response;
      }

      User springUser = new User();
      springUser.setUsername(userInfoDTO.getUsername());
      springUser.setEmail(userInfoDTO.getEmail());
      springUser.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));

      userRepository.save(springUser);

      response.put("message", "user registered successfully");
      return response;
    } catch (Exception e) {
      response.put("error", e.getMessage());
      return response;
    }
  }

  public Map<String, String> login(UserInfoDTO userInfo, HttpServletResponse response) {
    Map<String, String> responses = new HashMap<>();

    if (userInfo.getUsername().isEmpty() || userInfo.getUsername() == null) {
      responses.put("err", "please provide username or email");
      return responses;
    }
    if (userInfo.getPassword().isEmpty() || userInfo.getPassword() == null) {
      responses.put("err", "please provide password");
      return responses;
    }

    User user = userRepository.findByUsername(userInfo.getUsername());

    if (user == null) {
      responses.put("error", "Invalid username");
      return responses;
    }

    boolean isPasswordValid = passwordEncoder.matches(userInfo.getPassword(), user.getPassword());

    if (!isPasswordValid) {
      responses.put("err", "Invalid Credentials");
      return responses;
    }

    try {
      String token = jwtTokenUtil.generateToken(userInfo.getUsername(), user.getId());
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

  public Map<String, Object> changeUserDetails(UserInfoDTO userInfo, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    try {
      String loggedInUsername = (String) request.getAttribute("username"); // Assume username is set in request

      if (loggedInUsername == null) {
        response.put("error", "User not authenticated");
        return response;
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

      response.put("message", "User details updated");
      response.put("user", user);
      return response;
    } catch (Exception e) {
      response.put("error", "Failed to update user details");
      return response;
    }
  }
}
