package com.example.todo.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefreshToken {
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expiryDate;
}
