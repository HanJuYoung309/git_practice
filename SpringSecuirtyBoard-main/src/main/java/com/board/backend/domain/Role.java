package com.board.backend.domain;


import lombok.Data;
import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER,
    ROLE_ADMIN;


    // 이 메서드도 중요합니다!
    public String getAuthority() {
        return name(); // Spring Security는 "ROLE_USER" 같은 문자열을 원합니다.
    }
}