package com.example.devtube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.devtube.service.LikeService;
import com.example.devtube.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    // video like toggle
    @GetMapping("/toggle-video-like")
    public ResponseEntity<ApiResponse> toggleVideoLike(
        @RequestParam(name = "videoId") int videoId, 
        HttpServletRequest request) {
            
        // Validate videoId
        if (videoId <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Please provide a valid videoId", null));
        }
        // Attempt to toggle the like
        boolean success = likeService.toggleLike(videoId,"Video", request);
        if (success) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(200, "Like status toggled successfully", null));
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(401, "Unable to toggle like. Unauthorized or invalid videoId.", null));
        }
    }

    // comment like toggle
    @GetMapping("/toggle-comment-like")
    public ResponseEntity<ApiResponse> commentLike(
        @RequestParam(name = "commentId") int commentId, 
        HttpServletRequest request
    ){
        if (commentId <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Please provide a valid commentId", null));
        }

        boolean result = likeService.toggleLike(commentId, "comment", request);

        if (result) {
            return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponse(200, "Like status toggled successfully", request));
        }
        else {
            return ResponseEntity
            .status(HttpStatus.OK)
            .body((new ApiResponse(401,"Unable to toggle like. Unauthorized or invalid commentId.",null)));
        }
    }
}
