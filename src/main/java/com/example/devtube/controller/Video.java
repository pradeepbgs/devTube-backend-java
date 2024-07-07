package com.example.devtube.controller;

import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.devtube.Repository.VideoRepository;
import com.example.devtube.Repository.userRepository;
import com.example.devtube.lib.ApiResponse;
import com.example.devtube.models.User;
import com.example.devtube.service.AuthServie;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/video")
public class Video {
@Autowired
private VideoRepository videoRepository;

@Autowired
private AuthServie authServie;

@Autowired
private userRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> upload (
        @RequestPart("title") String title,
        @RequestPart("description") String description,
        @RequestPart("video") MultipartFile video,
        @RequestPart("thumbnail") MultipartFile thumbnail,
        HttpServletRequest request){
        if (title.isEmpty() || title == null) {
            ApiResponse apiResponse = new ApiResponse(400, "pls provide title", null);
            return ResponseEntity.ok(apiResponse);
        }
        if (description.isEmpty() || description == null) {
            ApiResponse apiResponse = new ApiResponse(400, "pls provide description", null);
            return ResponseEntity.ok(apiResponse);
        }
        if (video.isEmpty() || video == null) {
            ApiResponse apiResponse = new ApiResponse(400, "pls provide video", null);
            return ResponseEntity.ok(apiResponse);
        }
        if (thumbnail.isEmpty() || thumbnail == null) {
            ApiResponse apiResponse = new ApiResponse(400, "pls provide thumbnail", null);
            return ResponseEntity.ok(apiResponse);
        }
        try {
             String loggedInUsername = authServie.getUserFromRequest(request);
            User user = userRepository.findByUsername(loggedInUsername);
            return null;
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(400,"internal server problem",null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }
}
