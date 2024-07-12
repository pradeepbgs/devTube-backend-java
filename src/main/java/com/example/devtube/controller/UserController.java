package com.example.devtube.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.devtube.Repository.userRepository;
import com.example.devtube.lib.ApiResponse;
import com.example.devtube.lib.JwtTokenUtil;
import com.example.devtube.models.User;
import com.example.devtube.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private userRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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

            response.put("message", "user registered successfully");
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestPart("username") String username,
            @RequestPart("password") String password,
            HttpServletResponse response) {

        Map<String, String> responses = new HashMap<>();

        if (username.isEmpty() || username == null) {
            responses.put("err", "please provide username or email");
            return ResponseEntity.badRequest().body(responses);
        }
        if (password.isEmpty() || password == null) {
            responses.put("err", "please provide password");
            return ResponseEntity.badRequest().body(responses);
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            responses.put("error", "Invalid username");
            return ResponseEntity.badRequest().body(responses);
        }

        boolean isPasswordValid = passwordEncoder.matches(password, user.getPassword());

        if (!isPasswordValid) {
            responses.put("err", "Invalid Credentials");
            return ResponseEntity.badRequest().body(responses);
        }

        try {
            // now we have to use jwt to generate token and set as cookies

            String token = jwtTokenUtil.generateToken(username,user.getId());

            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            responses.put("message", "login successfull");

            return ResponseEntity.status(200).body(responses);
        } catch (Exception e) {
            responses.put("error", e.getMessage());
            return ResponseEntity.status(400).body(responses);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        ApiResponse apiResponse = new ApiResponse(200, "logout successfull",cookie);
        return ResponseEntity.status(200).body(apiResponse);
    }

    @PutMapping("/change-user-details")
    public ResponseEntity<ApiResponse> changeDetails(
        @RequestPart(required = false) String username,
        @RequestPart(required = false) String email,
        @RequestPart(required = false) String password,
        HttpServletRequest request
    ) {
        try {
            String loggedInUsername = authService.getUserFromRequest(request);
    
            if (loggedInUsername == null) {
                throw new Exception("User not authenticated");
            }
    
            User user = userRepository.findByUsername(loggedInUsername);
    
            if (username != null && !username.isEmpty()) {
                user.setUsername(username);
            }
            if (email != null && !email.isEmpty()) {
                user.setEmail(email);
            }
            if (password != null && !password.isEmpty()) {
                String hashedPassword = passwordEncoder.encode(password);
                user.setPassword(hashedPassword);
            }
    
            userRepository.save(user);
    
            ApiResponse apiResponse = new ApiResponse(200, "User details updated", user);
            return ResponseEntity.status(200).body(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(400, "Failed to update user details", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }
    

}
