package com.back.domain.member.member.dto;

import com.back.domain.member.member.entity.Member;

import java.time.LocalDateTime;

public record MemberDto(
        int id,                    // 회원 ID
        LocalDateTime createDate, // 생성일
        LocalDateTime modifyDate, // 수정일
        String name
) {
    //기본 생성자
    public MemberDto(int id, LocalDateTime createDate, LocalDateTime modifyDate, String name) {
        this.id = id;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.name = name;
    }

    //  Entity → DTO 변환자
    public MemberDto(Member member) {
        this(
                member.getId(),
                member.getCreateDate(),
                member.getModifyDate(),
                member.getName()
        );
    }
}
