package com.example.devtube.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.devtube.lib.ApiResponse;

@RestController
@RequestMapping("/api/comment")
public class Comment {
    @PostMapping("/add")
    public ResponseEntity<ApiResponse(
        @RequestParam("videoId") String video_id,
        @RequestParam("comment") String comment
    ){

    }
}
