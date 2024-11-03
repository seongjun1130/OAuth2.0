package com.sparta.oauth2_0.domain.member.entity;

import com.sparta.oauth2_0.api.config.Timestamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "members")
public class Member extends Timestamped {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  private String provider;

  private Long providerId;

  private String nickname;
}
