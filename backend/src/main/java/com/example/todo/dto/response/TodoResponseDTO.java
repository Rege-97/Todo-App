package com.example.todo.dto.response;

import com.example.todo.domain.Todos;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TodoResponseDTO {
    private long id;
    private String title;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    public TodoResponseDTO(Todos todos) {
        this.id = todos.getId();
        this.title = todos.getTitle();
        this.status = todos.getStatus().name();
        this.createdAt = todos.getCreatedAt();
        this.completedAt = todos.getCompletedAt();
    }
}
