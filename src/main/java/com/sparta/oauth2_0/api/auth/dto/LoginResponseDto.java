package com.sparta.oauth2_0.api.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
  private Long id;
  private String provider;
  private Long providerId;
  private String nickname;
  private String email;
  private LocalDateTime createdAt;
}
