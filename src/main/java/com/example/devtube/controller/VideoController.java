package com.example.devtube.controller;

import com.example.devtube.service.AuthService;
import com.example.devtube.service.VideoService;
import com.example.devtube.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

  @Autowired
  private VideoService videoService;

  @Autowired
  private AuthService authService;

  @GetMapping("/all-videos")
  public ResponseEntity<ApiResponse> getAllVideos(@RequestParam(defaultValue = "1") int page) {
    ApiResponse response = videoService.getVideos(page);
    if (response.getData() == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "No videos found", null));
    }
    return ResponseEntity.ok(response);
  }

  @PostMapping("/upload")
  public ResponseEntity<ApiResponse> uploadVideo(
    @RequestParam("thumbnail") MultipartFile thumbnail,
    @RequestParam("title") String title,
    @RequestParam("description") String description,
    @RequestParam("video") MultipartFile video,
    HttpServletRequest request
  ) {
    String loggedInUsername = authService.getUserFromRequest(request);
    ApiResponse response = videoService.uploadVideo(thumbnail, title, description, video, loggedInUsername);

    if (response.getStatus() == HttpStatus.OK.value()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @GetMapping("/get-videos")
  public ResponseEntity<ApiResponse> getVideos(@RequestParam("page") int page) {
    ApiResponse response = videoService.getVideos(page);
    if (response.getData() == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "No videos found", null));
    }
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user-videos")
  public ResponseEntity<ApiResponse> getUserVideos(
    @RequestParam("username") String username,
    @RequestParam("page") int page
  ) {
    ApiResponse response = videoService.getUserVideos(username, page);

    if (response.getData() == null) {
      return ResponseEntity.ok(new ApiResponse(200, "User has no videos", null));
    }
    return ResponseEntity.ok(response);
  }

  @PutMapping("/update")
  public ResponseEntity<ApiResponse> updateVideoDetails(
    @RequestParam("title") String title,
    @RequestParam("description") String description,
    @RequestParam("videoId") int videoId,
    @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
    HttpServletRequest request
  ) {
    String loggedInUsername = authService.getUserFromRequest(request);
    ApiResponse response = videoService.updateVideoDetails(title, description, videoId, thumbnail, loggedInUsername);

    if (response.getStatus() == HttpStatus.OK.value()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<ApiResponse> deleteVideo(@RequestParam("videoId") int videoId, HttpServletRequest request) {
    String loggedInUsername = authService.getUserFromRequest(request);
    ApiResponse response = videoService.deleteVideo(videoId, loggedInUsername);

    if (response.getStatus() == HttpStatus.OK.value()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @GetMapping("/{videoId}/comments")
  public ResponseEntity<ApiResponse> getVideoComments(
    @PathVariable("videoId") int videoId,
    @RequestParam("page") int page
  ) {
    if (page < 1) {
      return ResponseEntity.badRequest().body(new ApiResponse(400, "Page number must be greater than 0", null));
    }

    ApiResponse response = videoService.getVideoComments(videoId, page);

    if (response.getData() == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        new ApiResponse(404, "No comments found for this video", null)
      );
    }
    return ResponseEntity.ok(response);
  }
}
