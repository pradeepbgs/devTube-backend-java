package com.example.devtube.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Integer id;
    private String comment;
    private String owner;
    private int videoId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public int getVideo() {
        return videoId;
    }
    public void setVideo(int videoId) {
        this.videoId = videoId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
