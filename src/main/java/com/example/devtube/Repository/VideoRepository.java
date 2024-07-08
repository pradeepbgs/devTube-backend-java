package com.example.devtube.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devtube.models.VideoModel;

public interface VideoRepository extends JpaRepository<VideoModel,Integer>{
    List<VideoModel> findByOwner(String owner);
}
