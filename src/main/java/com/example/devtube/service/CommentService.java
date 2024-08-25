
package com.example.devtube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.devtube.entities.Comment;
import com.example.devtube.entities.User;
import com.example.devtube.entities.Video;
import com.example.devtube.repository.CommentRepository;
import com.example.devtube.repository.VideoRepository;
import com.example.devtube.repository.userRepository;
import com.example.devtube.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author pradeep
 */

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private userRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    public ApiResponse addComment(int videoId, String comment, HttpServletRequest request) {
        if (comment == null || comment.isEmpty()) {
            return new ApiResponse(400, "Please provide a comment", null);
        }

        String loggedInUserName = authService.getUserFromRequest(request);
        User user = userRepository.findByUsername(loggedInUserName);
        if (user == null) {
            return new ApiResponse(400, "User doesn't exist", null);
        }

        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            return new ApiResponse(400, "Video doesn't exist", null);
        }

        Comment commentModel = new Comment();
        commentModel.setComment(comment);
        commentModel.setOwner(loggedInUserName);
        commentModel.setVideo(video.getId());
        commentRepository.save(commentModel);

        return new ApiResponse(200, "Comment added successfully", null);
    }

    public ApiResponse deleteComment(int commentId, HttpServletRequest request) {
        String loggedInUserName = authService.getUserFromRequest(request);
        if (loggedInUserName == null || loggedInUserName.isEmpty()) {
            return new ApiResponse(400, "Unauthorized", null);
        }

        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return new ApiResponse(404, "Comment not found", null);
        }

        if (!loggedInUserName.equals(comment.getOwner())) {
            return new ApiResponse(400, "You are not the owner of this comment", null);
        }

        commentRepository.delete(comment);
        return new ApiResponse(200, "Comment deleted successfully", null);
    }

    public ApiResponse changeComment(int commentId, String comment, HttpServletRequest request) {
        if (comment == null || comment.isEmpty()) {
            return new ApiResponse(400, "Please provide a comment to change", null);
        }

        String loggedInUserName = authService.getUserFromRequest(request);
        if (loggedInUserName == null || loggedInUserName.isEmpty()) {
            return new ApiResponse(400, "Unauthorized", null);
        }

        Comment existingComment = commentRepository.findById(commentId).orElse(null);
        if (existingComment == null) {
            return new ApiResponse(404, "Comment not found", null);
        }

        if (!loggedInUserName.equals(existingComment.getOwner())) {
            return new ApiResponse(400, "You are not the owner of this comment", null);
        }

        existingComment.setComment(comment);
        commentRepository.save(existingComment);

        return new ApiResponse(200, "Comment updated successfully", existingComment);
    }
}
