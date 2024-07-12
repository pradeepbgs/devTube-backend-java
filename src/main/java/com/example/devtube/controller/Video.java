package com.example.devtube.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.devtube.Repository.VideoRepository;
import com.example.devtube.Repository.userRepository;
import com.example.devtube.lib.ApiResponse;
import com.example.devtube.lib.FileUploader;
import com.example.devtube.models.User;
import com.example.devtube.models.VideoModel;
import com.example.devtube.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/video")
public class Video {
@Autowired
private VideoRepository videoRepository;

@Autowired
private AuthService authService;

@Autowired
private userRepository userRepository;

@Autowired
private FileUploader fileUploader;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllVideos() {
        ApiResponse apiResponse = new ApiResponse(200, "all videos", videoRepository.findAll());
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> upload (
        @RequestPart("thumbnail") MultipartFile thumbnail,
        @RequestPart("title") String title,
        @RequestPart("description") String description,
        @RequestPart("video") MultipartFile video,
        HttpServletRequest request){
            System.out.println("hello");
            System.out.println(thumbnail);
            System.out.println(video);
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
             String loggedInUsername = authService.getUserFromRequest(request);
             User user = userRepository.findByUsername(loggedInUsername);
             if (user == null) {
                ApiResponse apiResponse = new ApiResponse(400, "user not found", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
             }
            
             boolean isVideoUploaded = fileUploader.uploadFile(video);
             boolean isThumbnailSaved = fileUploader.uploadFile(thumbnail);
            if (!isVideoUploaded || !isThumbnailSaved) {
                ApiResponse apiResponse = new ApiResponse(400, "didn't saved file`", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
            }
             
            String videoUrl = fileUploader.getFilePath(video); 
            String thumbnailUrl = fileUploader.getFilePath(thumbnail);
            VideoModel videoModel = new VideoModel();
            videoModel.setTitle(title);
            videoModel.setDescription(description);
            videoModel.setThumbnailUrl(thumbnailUrl);  
            videoModel.setUrl(videoUrl); 
            videoModel.setOwner(loggedInUsername);
            videoModel.setCreatedAt(LocalDateTime.now());

            videoRepository.save(videoModel);

            ApiResponse apiResponse = new ApiResponse(200,"file saved sucessfully", null);
            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {

            ApiResponse apiResponse = new ApiResponse(400,"internal server problem",null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);

        }
    }

    @GetMapping("/user-videos")
    public ResponseEntity<ApiResponse> getUserVideos(
        @RequestParam("username") String username,
        @RequestParam(value = "page", defaultValue = "1") int page
        ){
        try {
            if (username == null || username.isEmpty()) {
                ApiResponse apiResponse = new ApiResponse(499, "pls provide username", null);
                return ResponseEntity.ok(apiResponse);
            }
            int start = (page - 1) * 10;
            java.util.List<VideoModel> userVideos = videoRepository.findByOwner(username)
            .subList(start, Math.min(start + 10, videoRepository.findByOwner(username).size()));

            if (userVideos.isEmpty()) {
                ApiResponse apiResponse = new ApiResponse(200, "User has no videos", null);
                return ResponseEntity.ok(apiResponse);
            } 

            ApiResponse apiResponse = new ApiResponse(200, "User videos fetched successfully", userVideos);
            return  ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
        ApiResponse apiResponse = new ApiResponse(400, "Failed to fetch user videos", null);
        return  ResponseEntity.ok(apiResponse);
        }
    }

    @PostMapping("/update-video-details")
    public ResponseEntity <ApiResponse> updateVideoDeatils(
        @RequestPart(required = false) String title,
        @RequestPart(required = false) String description,
        @RequestParam("id") int video_id,
        @RequestPart(required = false) MultipartFile thumbnail,
        HttpServletRequest request){
            try {
                String loggedInUsername = authService.getUserFromRequest(request);
                User user = userRepository.findByUsername(loggedInUsername);
                if (user == null) {
                    ApiResponse apiResponse = new ApiResponse(400, "User not found", null);
                    return ResponseEntity.ok(apiResponse);
                }
                VideoModel video = videoRepository.findById(video_id).orElse(null);
                if (video == null) {
                    ApiResponse apiResponse = new ApiResponse(400, "Video not found", null);
                    return ResponseEntity.ok(apiResponse);
                }
                if (!video.getOwner().equals(loggedInUsername)) {
                    ApiResponse apiResponse = new ApiResponse(400, "You are not the owner of this video", null);
                    return ResponseEntity.ok(apiResponse);
                }
                if (title != null && !title.isEmpty()) {
                    video.setTitle(title);
                }
                if (description != null && !description.isEmpty()) {
                    video.setDescription(description);
                }
                if (thumbnail != null && !thumbnail.isEmpty()) {
                    boolean isThumbnailSaved = fileUploader.uploadFile(thumbnail);
                    if (isThumbnailSaved) {
                        String thumbnailUrl = fileUploader.getFilePath(thumbnail);
                        video.setThumbnailUrl(thumbnailUrl);
                    }
                }
                videoRepository.save(video);
                ApiResponse apiResponse = new ApiResponse(200, "Video details updated successfully", video);
                return ResponseEntity.ok(apiResponse);
            } catch (Exception e) {
                ApiResponse apiResponse = new ApiResponse(400, "Failed to update video details", null);
                return ResponseEntity.ok(apiResponse);
            }
        }
    
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> deleteVideo(
        @RequestParam("id") int video_id,
        HttpServletRequest request){
      try {
        String loggedInUserName = authService.getUserFromRequest(request);
        User user = userRepository.findByUsername(loggedInUserName);

        if (user == null) {
            ApiResponse apiResponse = new ApiResponse(
                400, 
                "User not found", 
                null);
            return ResponseEntity.ok(apiResponse);
        }

        VideoModel video = videoRepository.findById(video_id).orElse(null);
        if (video == null) {
            ApiResponse apiResponse = new ApiResponse(400, "couldn't find video", null);
            return ResponseEntity.ok(apiResponse);
        }
        videoRepository.delete(video);

        ApiResponse apiResponse = new ApiResponse(
            200, 
            "video deleted successfully", 
            null
            );
        return ResponseEntity.ok(apiResponse);

      } catch (Exception e) {
        ApiResponse apiResponse = new ApiResponse(400, "internal server error", null);
        return ResponseEntity.ok(apiResponse);
      }
    }
}
