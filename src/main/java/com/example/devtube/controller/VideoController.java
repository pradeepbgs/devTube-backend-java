package com.example.devtube.controller;

import java.util.concurrent.CompletableFuture;

import com.example.devtube.service.AuthService;
import com.example.devtube.service.VideoService;
import com.example.devtube.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private AuthService authService;

    @GetMapping("/all-videos")
    public CompletableFuture<ResponseEntity<ApiResponse>> getAllVideos(@RequestParam(defaultValue = "1") int page) {
        return videoService.getVideos(page)
                .thenApply(response -> {
                    if (response.getData() == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse(404, "No videos found", null));
                    }
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse(500, "An error occurred while processing the request", null));
                });
    }

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<ApiResponse>> uploadVideo(
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("video") MultipartFile video,
            HttpServletRequest request) {
        return videoService
                .uploadVideo(thumbnail, title, description, video, request)
                .thenApply(response -> ResponseEntity.ok(response))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse(500, "An error occurred while processing the request", null));
                });
    }

    @GetMapping("/get-videos")
    public CompletableFuture<ResponseEntity<ApiResponse>> getVideos(@RequestParam("page") int page) {
        return videoService
                .getVideos(page)
                .thenApply(response -> {
                    if (response.getData() == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "No videos found", null));
                    }
                    return ResponseEntity.ok(response);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse(500, "An error occurred while processing the request", null));
                });
    }

    @GetMapping("/user-videos")
    public CompletableFuture<ResponseEntity<ApiResponse>> getUserVideos(
            @RequestParam("username") String username,
            @RequestParam("page") int page) {
        return videoService
                .getUserVideos(username, page)
                .thenApply(res -> {
                    if (res.getData() == null) {
                        return ResponseEntity.ok(new ApiResponse(200, "User has no videos", null));
                    }
                    return ResponseEntity.ok(res);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            new ApiResponse(500, "An error occurred while processing the request", null));
                });
    }

    @PutMapping("/update")
    public CompletableFuture<ResponseEntity<ApiResponse>> updateVideoDetails(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("videoId") int videoId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            HttpServletRequest request) {
        String loggedInUsername = authService.getUserFromRequest(request);
        return videoService
                .updateVideoDetails(title, description, videoId, thumbnail, loggedInUsername)
                .thenApply(res -> ResponseEntity.ok(res))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            new ApiResponse(500, "An error occurred while processing the request", null));
                });

    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<ApiResponse>> deleteVideo(@RequestParam("videoId") int videoId,
            HttpServletRequest request) {
        if (videoId <= 0) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new ApiResponse(404, "please provide video id", null)));
        }
        String loggedInUsername = authService.getUserFromRequest(request);
        return videoService
                .deleteVideo(videoId, loggedInUsername)
                .thenApply(res -> {
                    if (res.getStatus() == HttpStatus.OK.value()) {
                        return ResponseEntity.ok(res);
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            new ApiResponse(500, "An error occurred while processing the request", null));
                });
    }

    @GetMapping("/comments/{videoId}")
    public CompletableFuture<ResponseEntity<ApiResponse>> getVideoComments(
            @PathVariable("videoId") int videoId,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        if (page < 1) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(new ApiResponse(400, "Page number must be greater than 0", null)));
        }

        // Fetch comments for the video
        return videoService
                .getVideoComments(videoId, page)
                .thenApply(res -> {
                    if (res.getData() == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ApiResponse(404, "No comments found for this video", null));
                    }
                    return ResponseEntity.ok(res);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            new ApiResponse(500, "An error occurred while processing the request", null));
                });
    }
}
