package com.example.devtube.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.devtube.entities.Comment;
import com.example.devtube.entities.User;
import com.example.devtube.entities.Video;
import com.example.devtube.repository.CommentRepository;
import com.example.devtube.repository.VideoRepository;
import com.example.devtube.repository.userRepository;
import com.example.devtube.utils.FileUploader;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;


    @Autowired
    private userRepository userRepository;

    @Autowired
    private FileUploader fileUploader;

    @Autowired
    private CommentRepository commentRepository;

    public boolean uploadVideo(
            MultipartFile thumbnail,
            String title,
            String description,
            MultipartFile video,
            String loggedInUsername) {

        if (title == null || title.isEmpty() || description == null || description.isEmpty() ||
            video == null || video.isEmpty() || thumbnail == null || thumbnail.isEmpty()) {
            return false; // Indicate validation failure
        }

        try {
            User user = userRepository.findByUsername(loggedInUsername);
            if (user == null) {
                return false; // User not found
            }

            boolean isVideoUploaded = fileUploader.uploadFile(video);
            boolean isThumbnailSaved = fileUploader.uploadFile(thumbnail);
            if (!isVideoUploaded || !isThumbnailSaved) {
                return false; // File upload failed
            }

            String videoUrl = fileUploader.getFilePath(video);
            String thumbnailUrl = fileUploader.getFilePath(thumbnail);
            Video videoModel = new Video();
            videoModel.setTitle(title);
            videoModel.setDescription(description);
            videoModel.setThumbnailUrl(thumbnailUrl);
            videoModel.setUrl(videoUrl);
            videoModel.setOwner(loggedInUsername);
            videoModel.setCreatedAt(LocalDateTime.now());

            videoRepository.save(videoModel);
            return true; // Success
        } catch (Exception e) {
            return false; // Internal server error
        }
    }

    public Page<Video> getVideos(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return videoRepository.findAll(pageable);
    }

    public Page<Video> getUserVideos(String username, int page) {
        if (username == null || username.isEmpty()) {
            return Page.empty(); // Indicate invalid input
        }

        Pageable pageable = PageRequest.of(page - 1, 10);
        return videoRepository.findByOwner(username, pageable);
    }

    public boolean updateVideoDetails(
            String title,
            String description,
            int videoId,
            MultipartFile thumbnail,
            String loggedInUsername) {

        try {
            User user = userRepository.findByUsername(loggedInUsername);
            if (user == null) {
                return false; // User not found
            }

            Video video = videoRepository.findById(videoId).orElse(null);
            if (video == null || !video.getOwner().equals(loggedInUsername)) {
                return false; // Video not found or unauthorized
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
            return true; // Success
        } catch (Exception e) {
            return false; // Internal server error
        }
    }

    public boolean deleteVideo(int videoId, String loggedInUsername) {
        try {
            User user = userRepository.findByUsername(loggedInUsername);
            if (user == null) {
                return false; // User not found
            }

            Video video = videoRepository.findById(videoId).orElse(null);
            if (video == null || !video.getOwner().equals(loggedInUsername)) {
                return false; // Video not found or unauthorized
            }

            videoRepository.deleteById(videoId);
            return true; // Success
        } catch (Exception e) {
            return false; // Internal server error
        }
    }

    public Page<Comment> getVideoComments(int videoId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return commentRepository.findByVideo(videoId, pageable);
    }
}
