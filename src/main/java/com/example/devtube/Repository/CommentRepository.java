package com.example.devtube.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.devtube.models.CommentModel;

public interface CommentRepository extends JpaRepository<CommentModel, Integer> {
    @Query("SELECT c FROM CommentModel c JOIN FETCH c.user WHERE c.videoId = :videoId")
    Page<CommentModel> findByVideoId(@Param("videoId") int videoId, Pageable pageable);
}
