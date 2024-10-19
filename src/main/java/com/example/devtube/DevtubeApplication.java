package com.example.devtube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableAsync
public class DevtubeApplication {

  public static void main(String[] args) {
    SpringApplication.run(DevtubeApplication.class, args);
  }

  @GetMapping("/")
  public String Hello() {
    return "hello from java";
  }
}
