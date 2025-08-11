package com.example.todo.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;          // PK
    private String email;     // 이메일
    private String password;  // BCrypt 해시 비밀번호
    private LocalDateTime createdAt; // 생성일 (created_at 매핑)
}
