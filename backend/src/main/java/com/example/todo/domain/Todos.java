package com.example.todo.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Todos {
    private long id;
    private long userId;
    private String title;
    private TodoStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
