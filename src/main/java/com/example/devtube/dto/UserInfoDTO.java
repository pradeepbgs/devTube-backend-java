package com.example.devtube.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class UserInfoDTO {

  private String username;
  private String lastName;
  private Long phoneNumber;
  private String email;
  private String password;
}
