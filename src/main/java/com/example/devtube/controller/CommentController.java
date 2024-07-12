package com.example.devtube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.devtube.Repository.CommentRepository;
import com.example.devtube.Repository.VideoRepository;
import com.example.devtube.Repository.userRepository;
import com.example.devtube.lib.ApiResponse;
import com.example.devtube.models.CommentModel;
import com.example.devtube.models.User;
import com.example.devtube.models.VideoModel;
import com.example.devtube.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    AuthService authService;

    @Autowired
    userRepository userRepository;

    @Autowired
    VideoRepository videoRepository;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addComment (
        @RequestParam("videoId") int video_id,
        @RequestParam("comment") String comment,
        HttpServletRequest request
    ){
        try {
            if (comment == null || comment.isEmpty()) {
                return createResponse(400, "Please provide a comment", null);
            }

            String loggedInUserName = authService.getUserFromRequest(request);
            User user = userRepository.findByUsername(loggedInUserName);
            if (user == null) {
                return createResponse(400, "User doesn't exist", null);
            }

            VideoModel video = videoRepository.findById(video_id).orElse(null);
            if (video == null) {
                return createResponse(400, "Video doesn't exist", null);
            }

            // Removed the check if the logged-in user is the owner of the video, 
            // as users can comment on any video, not just their own.
            CommentModel commentModel = new CommentModel();
            commentModel.setComment(comment);
            commentModel.setUser(loggedInUserName);
            commentModel.setVideo(video.getId());
            commentRepository.save(commentModel);

            return createResponse(200, "Comment added successfully", null);

        } catch (Exception e) {
            return createResponse(500, "Internal server error while adding comment", null);
        }
    }

    private ResponseEntity<ApiResponse> createResponse(int status, String message, Object data) {
        ApiResponse apiResponse = new ApiResponse(status, message, data);
        return ResponseEntity.status(status).body(apiResponse);
    }
}
