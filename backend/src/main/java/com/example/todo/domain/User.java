package com.example.todo.domain;

import lombok.Data;

@Data
public class User {
    private Long id;          // PK
    private String email;     // 이메일
    private String password;  // BCrypt 해시 비밀번호
    private String createdAt; // 생성일 (created_at 매핑)
}
