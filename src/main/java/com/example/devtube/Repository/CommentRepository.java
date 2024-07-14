package com.example.devtube.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devtube.models.CommentModel;

public interface CommentRepository extends JpaRepository <CommentModel,Integer> {
    List<CommentModel> findByVideoId(int videoId);
}

