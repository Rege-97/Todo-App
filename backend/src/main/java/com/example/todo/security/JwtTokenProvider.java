package com.example.todo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

// JWT 생성/파싱 유틸
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;  // 토큰 유효기간

    private Key key;    // 서명에 쓰일 비밀 키 객체

    @PostConstruct
    void init() {
        // HMAC-SHA 키 생성
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String generateToken(String email) {
        Date now = new Date();  // 토큰 생성시간
        Date expiryDate = new Date(now.getTime() + expirationMs);   // 토큰 종료 시간

        return Jwts.builder()
                .subject(email) // 로그인 식별자 저장
                .issuedAt(now)  // 발급 시각
                .expiration(expiryDate) // 토큰 종료시간
                .signWith(key)  // 서명
                .compact(); // 최종 직렬화된 JWT 문자열
    }

    // 토큰 파싱
    public String getSubject(String token) {
        return Jwts.parser()    // 파서 빌더 시작
                .verifyWith((SecretKey) key)   // 검증에 사용할 키 등록(SecretKey) 타입으로 형변환
                .build()    // 빌딩 완료
                .parseSignedClaims(token)   // 서명 검증 및 페이로드 파싱(서명이 틀리거나 만료되면 예외 발생)
                .getPayload()   // 페이로드에서
                .getSubject();  // 식별자 반환
    }
}
