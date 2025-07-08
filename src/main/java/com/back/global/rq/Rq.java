package com.back.global.rq;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // Lombok을 사용하여 생성자를 자동으로 생성합니다.
public class Rq {
    private final MemberService memberService;
    private final HttpServletRequest request;

    public Member getActor() {
        String headerAuthorization = request.getHeader("Authorization");

        if(headerAuthorization == null || headerAuthorization.isBlank()) // Authorization 헤더가 없거나 비어있으면 null 반환
            throw new ServiceException("401-1", "Authorization 헤더가 존재하지 않습니다.");

        if(!headerAuthorization.startsWith("Bearer ")) // Authorization 헤더가 Bearer 형식이 아니면 예외 발생
            throw new ServiceException("401-2", "Authorization 헤더가 Bearer 형식이 아닙니다.");

        String apiKey = headerAuthorization.substring("Bearer ".length()).trim(); // Bearer 접두사를 제거하고 공백을 제거한 API 키를 추출

        Member member = memberService // API 키로 회원을 조회
                .findByApiKey(apiKey) // findByApiKey 메서드를 사용하여 API 키로 회원을 조회
                .orElseThrow(() -> new ServiceException("401-3", "API 키가 유효하지 않습니다.")); // 조회된 회원이 없으면 예외 발생

        return member; // 유효한 API 키로 회원을 조회하여 반환
    }
}