package com.example.devtube.UserController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * UserController
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping("/register",consumes=
    {MediaType.MULTIPART_FORM_DATA_VALUE})

    public ResponseEntity<Map<String, String>> register(
            @RequestPart("usernamr") String username,
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart("avatar") MultipartFile avatar) {

                Map<String, String> response = new HashMap<>();

                if (username == null || username.isEmpty()) {
                    response.put("error", "username cannot be empty");
                    return ResponseEntity.badRequest().body(response);
                }
                if (email == null || email.isEmpty()) {
                    response.put("error", "email cannot be empty");
                    return ResponseEntity.badRequest().body(response);
                }
                if (password == null || password.isEmpty()) {
                    response.put("error", "password cannot be empty");
                    return ResponseEntity.badRequest().body(response);
                }

                try {
                    
                } catch (Exception e) {
                    response.put("error", e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
    }

}