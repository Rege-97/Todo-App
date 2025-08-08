package com.example.todo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 64)
    private String password;
}
