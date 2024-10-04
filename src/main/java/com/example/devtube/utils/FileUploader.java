package com.example.devtube.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploader {
    public final String UPLOAD_DIR="/home/pradeep/Desktop/code/devtube-java/src/main/resources/static/images";
    public boolean uploadFile(MultipartFile multipartFile){
        boolean isUploaded = false;

        try {
            // reading the data
            // InputStream file = multipartFile.getInputStream();
            // byte data[] = new byte[file.available()];
            // file.read(data);
            // // wil write the data in path
            // FileOutputStream fos = new FileOutputStream(UPLOAD_DIR+"/"+multipartFile.getOriginalFilename());
            // fos.write(data);
            // fos.flush();
            // fos.close();

            // or we can do like this tooo

            Files.copy(multipartFile.getInputStream(), Paths.get(UPLOAD_DIR+File.separator+multipartFile.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            isUploaded = true; 

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return isUploaded;
    }

    public String getFilePath(MultipartFile multipartFile){
        return UPLOAD_DIR+File.separator+multipartFile.getOriginalFilename();
    }
}
