package com.example.devtube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.devtube.lib.ApiResponse;
import com.example.devtube.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addComment(
            @RequestParam("videoId") int videoId,
            @RequestParam("comment") String comment,
            HttpServletRequest request
    ) {
        ApiResponse response = commentService.addComment(videoId, comment, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteComment(
            @RequestParam("commentId") int commentId,
            HttpServletRequest request
    ) {
        ApiResponse response = commentService.deleteComment(commentId, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/change")
    public ResponseEntity<ApiResponse> changeComment(
            @RequestParam("commentId") int commentId,
            @RequestParam("comment") String comment,
            HttpServletRequest request
    ) {
        ApiResponse response = commentService.changeComment(commentId, comment, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
