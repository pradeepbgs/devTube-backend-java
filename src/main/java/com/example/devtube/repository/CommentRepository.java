package com.example.devtube.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devtube.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByVideoId(int videoId, Pageable pageable);
}
