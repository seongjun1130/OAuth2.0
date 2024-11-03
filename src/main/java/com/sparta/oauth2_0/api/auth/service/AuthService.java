package com.sparta.oauth2_0.api.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oauth2_0.api.auth.dto.LoginResponseDto;
import com.sparta.oauth2_0.api.auth.dto.UserInfoDto;
import com.sparta.oauth2_0.domain.member.entity.Member;
import com.sparta.oauth2_0.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final MemberRepository memberRepository;

  @Value("${oauth2.kakao.client-secret}")
  private String kakaoKey;

  @Value("${oauth2.kakao.redirect-uri}")
  private String kakaoRedirectUri;

  public LoginResponseDto kakaoLogin(String code) throws JsonProcessingException {
    String accessToken = getKakaoAccessToken(code);
    UserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
    Member kakaoMember = registerSocialMemberIfNeeded(kakaoUserInfo);
    return LoginResponseDto.builder()
        .id(kakaoMember.getId())
        .nickname(kakaoMember.getNickname())
        .email(kakaoMember.getEmail())
        .provider(kakaoMember.getProvider())
        .providerId(kakaoMember.getProviderId())
        .createdAt(kakaoMember.getCreatedAt())
        .build();
  }

  private String getKakaoAccessToken(String code) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", kakaoKey);
    body.add("redirect_uri", kakaoRedirectUri);
    body.add("code", code);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response =
        restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", request, String.class);

    JsonNode node = objectMapper.readTree(response.getBody());
    return node.get("access_token").asText();
  }

  private UserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    RequestEntity<MultiValueMap<String, String>> requestEntity =
        RequestEntity.post("https://kapi.kakao.com/v2/user/me")
            .headers(headers)
            .body(new LinkedMultiValueMap<>());

    ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
    JsonNode jsonNode = objectMapper.readTree(response.getBody());
    Long id = jsonNode.get("id").asLong();
    String nickname = jsonNode.get("properties").get("nickname").asText();
    String email = jsonNode.get("kakao_account").get("email").asText();
    return new UserInfoDto(id, nickname, email, "kakao");
  }

  private Member registerSocialMemberIfNeeded(UserInfoDto userInfo) {
    Member socialMember = memberRepository.findByProviderId(userInfo.getId()).orElse(null);
    if (socialMember != null) {
      return socialMember;
    }
    if (memberRepository.existsByEmail(userInfo.getEmail())) {
      throw new IllegalArgumentException("이메일이 이미 등록되어 있습니다. 다른 소셜 로그인으로 로그인해주세요.");
    }
    Member member =
        Member.builder()
            .email(userInfo.getEmail())
            .providerId(userInfo.getId())
            .provider(userInfo.getProvider())
            .nickname(userInfo.getNickname())
            .build();
    memberRepository.save(member);
    return member;
  }
}
