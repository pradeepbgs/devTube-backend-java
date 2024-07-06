package com.example.devtube.UserController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import com.example.devtube.Repository.userRepository;
import com.example.devtube.lib.ApiResponse;
import com.example.devtube.models.User;
import com.example.devtube.lib.ApiResponse;

/**
 * UserController
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private userRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String greeting() {
        return "Hello, World!, from spring";
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestPart("username") String username,
            @RequestPart("email") String email,
            @RequestPart("password") String password
    // @RequestPart("avatar") MultipartFile avatar
    ) {

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
          
            boolean existingUser = userRepository.existsByUsernameOrEmail(username, email);
            if (existingUser) {
                response.put("error", "Username or email already exists");
                return ResponseEntity.status(400).body(response);
            }
            User springUser = new User();
            springUser.setUsername(username);

            String hashedPassword = passwordEncoder.encode(password);
            springUser.setPassword(hashedPassword);

            springUser.setEmail(email);

            userRepository.save(springUser);

            if (!existingUser) {
                response.put("error", "User not registered");
                return ResponseEntity.status(400).body(response);
            }

            response.put("message", "user registered successfully");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
