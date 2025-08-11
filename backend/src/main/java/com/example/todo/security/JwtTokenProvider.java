package com.example.todo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT 생성/파싱 유틸
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long accessTokenExpirationMs;   // 액세스 토큰 유효기간

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;  // 리프레쉬 토큰 유효기간

    private SecretKey key;    // 서명에 쓰일 비밀 키 객체

    @PostConstruct
    void init() {
        // HMAC-SHA 키 생성
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 액세스 토큰 생성
    public String generateAccessToekn(Long id, String email) {
        Date now = new Date();  // 토큰 생성시간
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);   // 토큰 종료 시간

        Claims claims = Jwts.claims()   // JWT에 직접 저장하는 신분증 같은 역할
                .subject(email)  // 로그인 식별자 저장
                .add("id", id)  // id 추가
                .build();

        return Jwts.builder()
                .claims(claims) // 클레임 저장
                .issuedAt(now)  // 발급 시각
                .expiration(expiryDate) // 토큰 종료시간
                .signWith(key)  // 서명
                .compact(); // 최종 직렬화된 JWT 문자열
    }

    // 리프레쉬 토큰 생성
    public String generateRefreshToken(Long id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .claim("id", id) // Refresh Token에는 사용자 id만 포함
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // 토큰 파싱
    public String getSubject(String token) {
        return Jwts.parser()    // 파서 빌더 시작
                .verifyWith(key)   // 검증에 사용할 키 등록(SecretKey) 타입으로 형변환
                .build()    // 빌딩 완료
                .parseSignedClaims(token)   // 서명 검증 및 페이로드 파싱(서명이 틀리거나 만료되면 예외 발생)
                .getPayload()   // 페이로드에서
                .getSubject();  // 식별자 반환
    }

    // 리프레쉬 토큰에서 사용자 ID 추출
    public Long getUserIdFromRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("id", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
}
