package com.board.backend.domain;


import lombok.Data;
import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER,
    ROLE_ADMIN;


    public String getAuthority() {
        return name(); // Spring Security는 "ROLE_USER" 같은 문자열을 원합니다.
    }
}