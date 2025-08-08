package com.example.todo.controller;

import com.example.todo.common.ApiResponseDTO;
import com.example.todo.dto.request.RegisterRequestDTO;
import com.example.todo.dto.response.UserResponseDTO;
import com.example.todo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO req) {
        UserResponseDTO res = userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.success(201, "회원가입 성공", res));
    }
}
