package com.example.todo.dto.response;

import com.example.todo.domain.Todos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoResponseDTO {
    private long id;
    private String title;
    private String status;
    private String createdAt;

    public TodoResponseDTO(Todos todos) {
        this.id = todos.getId();
        this.title = todos.getTitle();
        this.status = todos.getStatus().name();
        this.createdAt = todos.getCreatedAt();
    }
}
