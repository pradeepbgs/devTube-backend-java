package com.example.devtube.controller;

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

import com.example.devtube.service.AuthService;
import com.example.devtube.service.VideoService;
import com.example.devtube.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private AuthService authService;

    @GetMapping("/all-videos")
    public ResponseEntity<ApiResponse> getAllVideos(){
        var videosPage = videoService.getVideos(1);
        if (videosPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "No videos found", null));
        }
        return ResponseEntity.ok(new ApiResponse(200, "Videos retrieved successfully", videosPage.getContent()));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadVideo(
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("video") MultipartFile video,
            HttpServletRequest request) {

        String loggedInUsername = authService.getUserFromRequest(request);
        boolean success = videoService.uploadVideo(thumbnail, title, description, video, loggedInUsername);

        if (success) {
            return ResponseEntity.ok(new ApiResponse(200, "File saved successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(400, "Failed to upload video", null));
        }
    }

    @GetMapping("/getvideos")
    public ResponseEntity<ApiResponse> getVideos(@RequestParam("page") int page) {
        var videosPage = videoService.getVideos(page);
        if (videosPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "No videos found", null));
        }
        return ResponseEntity.ok(new ApiResponse(200, "Videos retrieved successfully", videosPage.getContent()));
    }

    @GetMapping("/user-videos")
    public ResponseEntity<ApiResponse> getUserVideos(
            @RequestParam("username") String username,
            @RequestParam("page") int page) {
        var videoPage = videoService.getUserVideos(username, page);
        if (videoPage.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(200, "User has no videos", null));
        }
        return ResponseEntity.ok(new ApiResponse(200, "User videos fetched successfully", videoPage.getContent()));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateVideoDetails(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("videoId") int videoId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            HttpServletRequest request) {

        String loggedInUsername = authService.getUserFromRequest(request);
        boolean success = videoService.updateVideoDetails(title, description, videoId, thumbnail, loggedInUsername);

        if (success) {
            return ResponseEntity.ok(new ApiResponse(200, "Video details updated successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(400, "Failed to update video details", null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteVideo(
            @RequestParam("videoId") int videoId,
            HttpServletRequest request) {

        String loggedInUsername = authService.getUserFromRequest(request);
        boolean success = videoService.deleteVideo(videoId, loggedInUsername);

        if (success) {
            return ResponseEntity.ok(new ApiResponse(200, "Video deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(400, "Failed to delete video", null));
        }
    }

    @GetMapping("/{videoId}/comments")
    public ResponseEntity<ApiResponse> getVideoComments(
            @PathVariable("videoId") int videoId,
            @RequestParam("page") int page) {
            
                if (page < 1) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(400, "Page number must be greater than 0", null));
                }

        var commentsPage = videoService.getVideoComments(videoId, page);

        if (commentsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "No comments found for this video", null));
        }
        
        return ResponseEntity.ok(new ApiResponse(200, "Comments fetched successfully", commentsPage.getContent()));
    }
}
