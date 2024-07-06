package com.example.devtube;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllers {
    @GetMapping("/")
    public String greeting() {
        return "Hello, World!";
    }
}
