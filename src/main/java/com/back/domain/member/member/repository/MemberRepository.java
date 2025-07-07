package com.back.domain.member.member.repository;

import com.back.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUsername(String username); // 사용자 이름으로 회원 조회

    Optional<Member> findByApiKey(String apiKey); // API 키로 회원 조회
}