package com.example.devtube.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class VideoModel {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Integer id;
    private String owner;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    // private Integer views;
    // private String duration;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getOwner(){
        return owner;
    }
    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    // public Integer getViews() {
    //     return views;
    // }

    // public void setViews(Integer views) {
    //     this.views = views;
    // }

    // public String getDuration() {
    //     return duration;
    // }

    // public void setDuration(String duration) {
    //     this.duration = duration;
    // }

    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
