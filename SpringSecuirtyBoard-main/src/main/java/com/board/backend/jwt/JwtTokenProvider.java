package com.board.backend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration}")
    private long tokenValidityInMilliseconds;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // "auth" 클레임이 없을 경우를 대비하여 기본값 설정 또는 예외 처리
        Object authClaim = claims.get("auth");
        String authString = (authClaim != null) ? authClaim.toString() : ""; // null 체크

        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        if (!authString.isEmpty()) { // 비어있지않은 경우에만 파싱
            authorities = Arrays.stream(authString.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        // Spring Security의 User 객체 사용. 패스워드는 토큰 기반 인증이므로 빈 문자열.
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                claims.getSubject(),
                "", // 패스워드 (토큰 기반 인증에서는 필요 없음)
                authorities // 파싱된 권한들
        );

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // "잘못된 JWT 서명입니다."
        } catch (ExpiredJwtException e) {
            // "만료된 JWT 토큰입니다."
        } catch (UnsupportedJwtException e) {
            // "지원되지 않는 JWT 토큰입니다."
        } catch (IllegalArgumentException e) {
            // "JWT 토큰이 잘못되었습니다."
        }
        return false;
    }
}