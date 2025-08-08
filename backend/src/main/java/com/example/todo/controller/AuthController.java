package com.example.todo.controller;

import com.example.todo.common.ApiResponseDTO;
import com.example.todo.dto.request.RegisterRequestDTO;
import com.example.todo.dto.response.UserResponseDTO;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 API", description = "회원가입 및 로그인 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Operation(
            summary = "회원가입",
            description = "이메일과 비밀번호로 회원가입을 진행",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "입력값 오류"),
                    @ApiResponse(responseCode = "409", description = "중복 이메일")
            })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO req) {
        UserResponseDTO res = userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.success(201, "회원가입 성공", res));
    }
}
