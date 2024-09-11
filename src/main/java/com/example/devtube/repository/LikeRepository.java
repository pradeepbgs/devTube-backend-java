package com.example.devtube.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.devtube.entities.Like;
import com.example.devtube.entities.User;

public interface  LikeRepository extends CrudRepository <Like,Integer> {
    Optional<Like> findByLikedByAndContentIdAndContentType(User 
    likedBy, int contentId, String contentType);
}
