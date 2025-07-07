package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import com.back.global.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public long count() {
        return memberRepository.count();
    } // 회원 수 조회

    public Member join(String username, String password, String nickname) {
        memberRepository // 회원가입 시 아이디 중복 체크
                .findByUsername(username)
                .ifPresent(_member -> {
                    throw new ServiceException("409-1", "이미 존재하는 아이디입니다.");
                });

        Member member = new Member(username, password, nickname);
        return memberRepository.save(member);
    }

    public Optional<Member> findByUsername(String username) { // 사용자 이름으로 회원 조회
        return memberRepository.findByUsername(username);
    }
    public  Optional<Member> findByApiKey(String apiKey) { // API 키로 회원 조회
        return memberRepository.findByApiKey(apiKey);
    }
}