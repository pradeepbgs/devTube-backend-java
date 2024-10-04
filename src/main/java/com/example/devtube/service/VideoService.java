package com.example.devtube.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.utils.ObjectUtils;
import com.example.devtube.entities.Comment;
import com.example.devtube.entities.User;
import com.example.devtube.entities.Video;
import com.example.devtube.repository.CommentRepository;
import com.example.devtube.repository.UserRepository;
import com.example.devtube.repository.VideoRepository;
import com.example.devtube.utils.ApiResponse;
import com.example.devtube.utils.CloudinaryService;
import com.example.devtube.utils.FileUploader;

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

  public ApiResponse uploadVideo(
    MultipartFile thumbnail,
    String title,
    String description,
    MultipartFile video,
    String loggedInUsername
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
      return new ApiResponse(
        HttpStatus.BAD_REQUEST.value(),
        "title or description or video or thumbnail cannot be empty",
        null
      );
    }

    try {
      User user = userRepository.findByUsername(loggedInUsername);
      if (user == null) {
        return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "cannot find user!", null);
      }
      // boolean isVideoUploaded = fileUploader.uploadFile(video);
      // boolean isThumbnailSaved = fileUploader.uploadFile(thumbnail);
      // if (!isVideoUploaded || !isThumbnailSaved) {
      //   return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "video upload failed on server!", null);
      // }

      // String videoUrl = fileUploader.getFilePath(video);
      // String thumbnailUrl = fileUploader.getFilePath(thumbnail);
 
      Map<String,Object> videoUploadResult = cloudinaryService.upload_file(video,"video");
      String videoUrl = (String) videoUploadResult.get("secure_url");

      Map<String,Object> thumbnailUploadResult = cloudinaryService.upload_file(thumbnail,"image");
      String thumbnailUrl = (String) thumbnailUploadResult.get("secure_url");

      Video videoModel = new Video();
      videoModel.setTitle(title);
      videoModel.setDescription(description);
      videoModel.setThumbnailUrl(thumbnailUrl);
      videoModel.setUrl(videoUrl);
      videoModel.setOwner(loggedInUsername);
      videoModel.setCreatedAt(LocalDateTime.now());

      videoRepository.save(videoModel);
      return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "successfully video uploaded", videoModel);
    } catch (Exception e) {
      return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "internal server error!", e.getMessage());
    }
  }

  public ApiResponse getVideos(int page) {
    if (page <= 0) {
      return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Page number must be greater than 0", null);
    }
    Pageable pageable = PageRequest.of(page - 1, 10);
    Page<Video> videos = videoRepository.findAll(pageable);
    return new ApiResponse(HttpStatus.OK.value(), "Videos fetched successfully", videos);
  }

  public ApiResponse getUserVideos(String username, int page) {
    if (username == null || username.isEmpty()) {
      return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Username cannot be empty", null);
    }
    if (page <= 0) {
      return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Page number must be greater than 0", null);
    }
    Pageable pageable = PageRequest.of(page - 1, 10);
    Page<Video> userVideos = videoRepository.findByOwner(username, pageable);
    return new ApiResponse(HttpStatus.OK.value(), "User's videos fetched successfully", userVideos);
  }

  public ApiResponse updateVideoDetails(
    String title,
    String description,
    int videoId,
    MultipartFile thumbnail,
    String loggedInUsername
  ) {
    try {
      User user = userRepository.findByUsername(loggedInUsername);
      if (user == null) {
        return new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found", null);
      }

      Video video = videoRepository.findById(videoId).orElse(null);
      if (video == null || !video.getOwner().equals(loggedInUsername)) {
        return new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Video not found or unauthorized", null);
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
      return new ApiResponse(HttpStatus.OK.value(), "Video updated successfully", video);
    } catch (Exception e) {
      return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", e.getMessage());
    }
  }

  public ApiResponse deleteVideo(int videoId, String loggedInUsername) {
    try {
      User user = userRepository.findByUsername(loggedInUsername);
      if (user == null) {
        return new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found", null);
      }

      Video video = videoRepository.findById(videoId).orElse(null);
      if (video == null || !video.getOwner().equals(loggedInUsername)) {
        return new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Video not found or unauthorized", null);
      }

      videoRepository.deleteById(videoId);
      return new ApiResponse(HttpStatus.OK.value(), "Video deleted successfully", null);
    } catch (Exception e) {
      return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", e.getMessage());
    }
  }

  public ApiResponse getVideoComments(int videoId, int page) {
    if (page <= 0) {
      return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Page number must be greater than 0", null);
    }
    Pageable pageable = PageRequest.of(page - 1, 10);
    Page<Comment> comments = commentRepository.findByVideoId(videoId, pageable);
    return new ApiResponse(HttpStatus.OK.value(), "Comments fetched successfully", comments);
  }
}
