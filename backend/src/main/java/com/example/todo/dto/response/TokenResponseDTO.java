package com.example.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDTO {
    private String accessToken; // JWT 문자열
    private String refreshToken;
    private String tokenType;   // "Bearer"
    private long expiresIn;     // 만료(ms)
}
