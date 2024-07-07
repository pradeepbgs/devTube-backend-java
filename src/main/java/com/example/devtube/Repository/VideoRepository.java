package com.example.devtube.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devtube.models.Video;

public interface VideoRepository extends JpaRepository<Video,Integer>{
    List<Video> findByOwner(String owner);
}
