package com.example.todo.controller;


import com.example.todo.common.ApiResponseDTO;
import com.example.todo.dto.request.TodoCreateRequestDTO;
import com.example.todo.dto.request.TodoUpdateRequestDTO;
import com.example.todo.dto.response.PageResponseDTO;
import com.example.todo.dto.response.TodoResponseDTO;
import com.example.todo.service.TodoService;
import com.example.todo.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TodoCreateRequestDTO req) {
        long userId = securityUtil.getCurrentUserId();
        TodoResponseDTO createTodo = todoService.create(userId, req.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.success(HttpStatus.CREATED.value(), "투두 생성 성공", createTodo));
    }

    @GetMapping
    public ResponseEntity<?> getList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        long userId = securityUtil.getCurrentUserId();
        PageResponseDTO<TodoResponseDTO> todos = todoService.getList(userId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.success(HttpStatus.OK.value(), "투두 목록 조회 성공", todos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        long userId = securityUtil.getCurrentUserId();
        TodoResponseDTO todos = todoService.get(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.success(HttpStatus.OK.value(), "투두 상세 조회 성공", todos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @Valid @RequestBody TodoUpdateRequestDTO req) {
        long userId = securityUtil.getCurrentUserId();
        TodoResponseDTO updateTodo = todoService.update(id, userId, req);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.success(HttpStatus.OK.value(), "투두 수정 성공", updateTodo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        long userId = securityUtil.getCurrentUserId();
        todoService.delete(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.success(HttpStatus.OK.value(), "투두 삭제 성공", null));
    }
}
