package com.example.devtube.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.devtube.entities.Like;
import com.example.devtube.entities.User;
import com.example.devtube.repository.LikeRepository;
import com.example.devtube.repository.UserRepository;
import com.example.devtube.utils.JwtTokenUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LikeService {

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LikeRepository likeRepository;

  public boolean toggleLike(int contentId, String contentType, HttpServletRequest request) {
    String token = jwtTokenUtil.extractTokenFromRequest(request);
    if (token == null) {
      return false; // Token invalid or not present
    }
    String username = jwtTokenUtil.getUsernameFromToken(token);
    if (username == null || username.isEmpty()) {
      return false;
    }

    User user = userRepository.findByUsername(username);
    if (user == null) {
      return false;
    }

    if (!likeRepository.existsById(contentId)) {
      return false;
    }

    Optional<Like> existingLike = likeRepository.findByLikedByAndContentIdAndContentType(user, contentId, contentType);

    if (existingLike.isPresent()) {
      // Unlike the content by deleting the existing like
      likeRepository.delete(existingLike.get());
    } else {
      // Like the content by creating a new Like entry
      Like newLike = Like.builder().contentId(contentId).contentType(contentType).likedBy(user).build();
      likeRepository.save(newLike);
    }
    return true; // like toggled
  }
}
