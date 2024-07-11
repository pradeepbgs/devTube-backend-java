package com.example.devtube.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devtube.models.CommentModel;

public interface CommentRepository extends JpaRepository <CommentModel,Integer> {

}

