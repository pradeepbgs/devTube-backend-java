package com.example.devtube.Repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devtube.models.VideoModel;

public interface VideoRepository extends JpaRepository<VideoModel,Integer>{
    Page<VideoModel> findByOwner(String owner,Pageable pageable);
}
