package com.example.devtube.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devtube.entities.Video;

public interface VideoRepository extends JpaRepository<Video,Integer>{
    Page<Video> findByOwner(String owner,Pageable pageable);
}
