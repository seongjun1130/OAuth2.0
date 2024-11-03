package com.sparta.oauth2_0.domain.member.repository;

import com.sparta.oauth2_0.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderId(Long id);

    boolean existsByEmail(String email);
}
