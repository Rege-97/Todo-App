package com.example.todo.dto.request;

import com.example.todo.domain.TodoStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoUpdateRequestDTO {
    private String title;
    private TodoStatus status;
}