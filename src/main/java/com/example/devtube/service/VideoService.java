package com.example.devtube.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.devtube.entities.Comment;
import com.example.devtube.entities.User;
import com.example.devtube.entities.Video;
import com.example.devtube.repository.CommentRepository;
import com.example.devtube.repository.UserRepository;
import com.example.devtube.repository.VideoRepository;
import com.example.devtube.utils.ApiResponse;
import com.example.devtube.utils.CloudinaryService;
import com.example.devtube.utils.FileUploader;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class VideoService {

  @Autowired
  private VideoRepository videoRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FileUploader fileUploader;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CloudinaryService cloudinaryService;

  @Autowired
  private AuthService authService;

  @Async
  public CompletableFuture<ApiResponse> uploadVideo(
    MultipartFile thumbnail,
    String title,
    String description,
    MultipartFile video,
    HttpServletRequest request
  ) {
    if (
      title == null ||
      title.isEmpty() ||
      description == null ||
      description.isEmpty() ||
      video == null ||
      video.isEmpty() ||
      thumbnail == null ||
      thumbnail.isEmpty()
    ) {
      return CompletableFuture.completedFuture(
        new ApiResponse(
          HttpStatus.BAD_REQUEST.value(),
          "title or description or video or thumbnail cannot be empty",
          null
        )
      );
    }

    try {
      String loggedInUsername = authService.getUserFromRequest(request);
      User user = userRepository.findByUsername(loggedInUsername);
      if (user == null) {
        return CompletableFuture.completedFuture(
          new ApiResponse(HttpStatus.BAD_REQUEST.value(), "cannot find the user!", null)
        );
      }

      Map<String, Object> videoUploadResult = cloudinaryService.upload_file(video, "video");
      String videoUrl = (String) videoUploadResult.get("secure_url");

      Map<String, Object> thumbnailUploadResult = cloudinaryService.upload_file(thumbnail, "image");
      String thumbnailUrl = (String) thumbnailUploadResult.get("secure_url");

      Video videoModel = Video.builder()
        .title(title)
        .description(description)
        .thumbnailUrl(thumbnailUrl)
        .url(videoUrl)
        .owner(loggedInUsername)
        .createdAt(LocalDateTime.now())
        .build();

      videoRepository.save(videoModel);
      return CompletableFuture.completedFuture(
        new ApiResponse(HttpStatus.OK.value(), "Video uploaded successfully", videoModel)
      );
    } catch (Exception e) {
      return CompletableFuture.completedFuture(
        new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", e.getMessage())
      );
    }
  }

  @Async
  public CompletableFuture<ApiResponse> getVideos(int page) {
    if (page <= 0) {
      return CompletableFuture.completedFuture(
        new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Page number must be greater than 0", null)
        );
    }
    Pageable pageable = PageRequest.of(page - 1, 10);
    Page<Video> videos = videoRepository.findAll(pageable);
    return CompletableFuture.completedFuture(
    new ApiResponse(HttpStatus.OK.value(), "Videos fetched successfully", videos));
  }

  @Async
  public CompletableFuture<ApiResponse> getUserVideos(String username, int page) {
    if (username == null || username.isEmpty()) {
      return CompletableFuture.completedFuture(
      new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Username cannot be empty", null));
    }
    if (page <= 0) {
      return CompletableFuture.completedFuture(
      new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Page number must be greater than 0", null));
    }
    Pageable pageable = PageRequest.of(page - 1, 10);
    Page<Video> userVideos = videoRepository.findByOwner(username, pageable);
    return CompletableFuture.completedFuture(
    new ApiResponse(HttpStatus.OK.value(), "User's videos fetched successfully", userVideos));
  }

  @Async
  public CompletableFuture<ApiResponse> updateVideoDetails(
    String title,
    String description,
    int videoId,
    MultipartFile thumbnail,
    String loggedInUsername
  ) {
    try {
      User user = userRepository.findByUsername(loggedInUsername);
      if (user == null) {
        return CompletableFuture.completedFuture(
          new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found", null));
      }

      Video video = videoRepository.findById(videoId).orElse(null);
      if (video == null || !video.getOwner().equals(loggedInUsername)) {
        return CompletableFuture.completedFuture(
          new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Video not found or unauthorized", null));
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
      return CompletableFuture.completedFuture(
        new ApiResponse(HttpStatus.OK.value(), "Video updated successfully", video));
    } catch (Exception e) {
      return CompletableFuture.completedFuture(
        new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", e.getMessage()));
    }
  }

  @Async
  public CompletableFuture <ApiResponse> deleteVideo(int videoId, String loggedInUsername) {
    try {
      User user = userRepository.findByUsername(loggedInUsername);
      if (user == null) {
        return CompletableFuture.completedFuture(
          new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found", null));
      }

      Video video = videoRepository.findById(videoId).orElse(null);
      if (video == null || !video.getOwner().equals(loggedInUsername)) {
        return CompletableFuture.completedFuture(
          new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Video not found or unauthorized", null));
      }
      String videoUrl = video.getUrl();
      String thumbnailUrl = video.getThumbnailUrl();

      String videoPublicId = extractPublicIdFromUrl(videoUrl);
      String thumbnailPublicId = extractPublicIdFromUrl(thumbnailUrl);

      cloudinaryService.delete_file(videoPublicId, "video");
      cloudinaryService.delete_file(thumbnailPublicId, "image");

      videoRepository.deleteById(videoId);
      return CompletableFuture.completedFuture(
        new ApiResponse(HttpStatus.OK.value(), "Video deleted successfully", null));
    } catch (Exception e) {
      return CompletableFuture.completedFuture(
        new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", e.getMessage()));
    }
  }

  @Async
  public CompletableFuture <ApiResponse> getVideoComments(int videoId, int page) {
    Pageable pageable = PageRequest.of(page - 1, 10);
    Page<Comment> comments = commentRepository.findByVideoId(videoId, pageable);
    return CompletableFuture.completedFuture(
      new ApiResponse(HttpStatus.OK.value(), "Comments fetched successfully", comments));
  }

  private String extractPublicIdFromUrl(String url) {
    // Split the URL by '/' and get the last part
    String[] parts = url.split("/");
    String lastPart = parts[parts.length - 1];

    // Remove the version and file extension
    String[] lastPartParts = lastPart.split("\\.");
    String publicId = lastPartParts[0]; // This will give you the public ID
    return publicId;
  }
}
