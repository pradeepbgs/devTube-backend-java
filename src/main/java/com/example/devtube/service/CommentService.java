/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.devtube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.devtube.Repository.CommentRepository;
import com.example.devtube.Repository.VideoRepository;
import com.example.devtube.Repository.userRepository;
import com.example.devtube.lib.ApiResponse;
import com.example.devtube.models.CommentModel;
import com.example.devtube.models.User;
import com.example.devtube.models.VideoModel;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author pradeep
 */
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private userRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Transactional
    public ApiResponse addComment(int videoId, String comment, HttpServletRequest request) {
        if (comment == null || comment.isEmpty()) {
            return new ApiResponse(400, "Please provide a comment", null);
        }

        String loggedInUserName = authService.getUserFromRequest(request);
        User user = userRepository.findByUsername(loggedInUserName);
        if (user == null) {
            return new ApiResponse(400, "User doesn't exist", null);
        }

        VideoModel video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            return new ApiResponse(400, "Video doesn't exist", null);
        }

        CommentModel commentModel = new CommentModel();
        commentModel.setComment(comment);
        commentModel.setOwner(loggedInUserName);
        commentModel.setVideo(video.getId());
        commentRepository.save(commentModel);

        return new ApiResponse(200, "Comment added successfully", null);
    }

    @Transactional
    public ApiResponse deleteComment(int commentId, HttpServletRequest request) {
        String loggedInUserName = authService.getUserFromRequest(request);
        if (loggedInUserName == null || loggedInUserName.isEmpty()) {
            return new ApiResponse(400, "Unauthorized", null);
        }

        CommentModel comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return new ApiResponse(404, "Comment not found", null);
        }

        if (!loggedInUserName.equals(comment.getOwner())) {
            return new ApiResponse(400, "You are not the owner of this comment", null);
        }

        commentRepository.delete(comment);
        return new ApiResponse(200, "Comment deleted successfully", null);
    }

    @Transactional
    public ApiResponse changeComment(int commentId, String comment, HttpServletRequest request) {
        if (comment == null || comment.isEmpty()) {
            return new ApiResponse(400, "Please provide a comment to change", null);
        }

        String loggedInUserName = authService.getUserFromRequest(request);
        if (loggedInUserName == null || loggedInUserName.isEmpty()) {
            return new ApiResponse(400, "Unauthorized", null);
        }

        CommentModel existingComment = commentRepository.findById(commentId).orElse(null);
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
