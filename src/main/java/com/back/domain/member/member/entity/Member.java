package com.back.domain.member.member.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID; // UUID를 사용하여 API 키를 생성하기 위해 import합니다.

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    @Column(unique = true) // 사용자 이름은 유일해야 합니다.
    private String username;
    private String password;
    private String nickname;
    @Column(unique = true) // API 키는 유일해야 합니다.
    private String apiKey;

    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.apiKey = UUID.randomUUID().toString(); // API 키는 UUID를 사용하여 생성합니다.
    }

    public String getName() {
        return nickname;
    }
}