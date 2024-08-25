package com.example.devtube.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.devtube.service.UserService;
import com.example.devtube.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String greeting() {
        return "Hello, World!, from spring";
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        Map<String, String> response = userService.register(username, email, password);
        if (response.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response) {

        Map<String, String> responses = userService.login(username, password, response);
        if (responses.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responses);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        Map<String, String> responseMap = userService.logout(response);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200, responseMap.get("message"), null));
    }

    @PutMapping("/change-user-details")
    public ResponseEntity<ApiResponse> changeDetails(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            HttpServletRequest request) {

        Map<String, Object> response = userService.changeUserDetails(username, email, password, request);
        if (response.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(400, (String) response.get("error"), null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200, (String) response.get("message"), response.get("user")));
    }
}
