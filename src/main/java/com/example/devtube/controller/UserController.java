package com.example.devtube.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.devtube.dto.UserInfoDTO;
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
    public ResponseEntity<ApiResponse> register(@RequestBody UserInfoDTO userInfo) {
        ApiResponse response = userService.register(userInfo);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserInfoDTO userInfo, HttpServletResponse response) {
        ApiResponse apiResponse = userService.login(userInfo, response);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PutMapping("/change-user-details")
    public CompletableFuture<ResponseEntity<ApiResponse>> changeDetails(@RequestBody UserInfoDTO userInfo,
            HttpServletRequest request) {
        return userService
                .changeUserDetails(userInfo, request)
                .thenApply(res -> ResponseEntity.ok(res))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            new ApiResponse(500, "An error occurred while processing the request", null));
                });
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        ApiResponse apiResponse = userService.logout(response);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
