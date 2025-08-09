package com.example.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoCreateRequestDTO {

    @NotBlank
    private String title;
}
