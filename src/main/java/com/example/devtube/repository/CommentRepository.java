package com.example.devtube.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.devtube.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByVideo(@Param("video") int video, Pageable pageable);
}
