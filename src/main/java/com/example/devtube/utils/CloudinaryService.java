package com.example.devtube.utils;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import jakarta.annotation.Resources;
//...
/**
 * Cloudinary
 */

 @Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    public Map<String,Object> upload_file(MultipartFile file, String resourceType) throws IOException{
        Map<String, Object> uploadOptions = ObjectUtils.asMap("resource_type", resourceType);
        return cloudinary.uploader().upload(file.getBytes(), uploadOptions);
    }

    public Map<String,Object> delete_file(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}