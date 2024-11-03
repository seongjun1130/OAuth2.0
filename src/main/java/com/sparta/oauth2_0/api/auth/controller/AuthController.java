package com.sparta.oauth2_0.api.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.oauth2_0.api.auth.dto.LoginResponseDto;
import com.sparta.oauth2_0.api.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class AuthController {

  private final AuthService authService;

  @Value("${oauth2.kakao.client-secret}")
  private String kakaoKey;

  // provider 에게 맞는 로그인 URI 및 redirect_uri 매칭
  @GetMapping("/{provider}/login")
  public RedirectView socialLogin(@PathVariable String provider) {
    if ("kakao".equals(provider)) {
      String redirectUrl =
          "https://kauth.kakao.com/oauth/authorize"
              + "?client_id="
              + kakaoKey
              + "&redirect_uri="
              + "http://localhost:8080/api/oauth2/kakao/callback"
              + "&response_type=code";
      return new RedirectView(redirectUrl);
    }
    throw new IllegalArgumentException("지원하지 않는 소셜로그인 폼 입니다.: " + provider);
  }

  @GetMapping("/kakao/callback")
  public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String code)
      throws JsonProcessingException {
    LoginResponseDto responseDto = authService.kakaoLogin(code);
    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
  }
}
