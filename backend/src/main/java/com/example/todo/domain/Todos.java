package com.example.todo.domain;

import lombok.Data;

@Data
public class Todos {
    private long id;
    private long userId;
    private String title;
    private TodoStatus status;
    private String createdAt;
}
