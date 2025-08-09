package com.example.todo.service;

import com.example.todo.domain.TodoStatus;
import com.example.todo.domain.Todos;
import com.example.todo.dto.request.TodoUpdateRequestDTO;
import com.example.todo.dto.response.PageInfo;
import com.example.todo.dto.response.PageResponseDTO;
import com.example.todo.dto.response.TodoResponseDTO;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoMapper todoMapper;

    // 투두 생성
    public TodoResponseDTO create(long userId, String title) {
        Todos todos = new Todos();
        todos.setUserId(userId);
        todos.setTitle(title);
        todos.setStatus(TodoStatus.TODO);
        todoMapper.insert(todos);
        return new TodoResponseDTO(todos);
    }

    // 투두 목록 조회
    public PageResponseDTO<TodoResponseDTO> getList(long userId, int page, int size, String keyword, LocalDate startDate, LocalDate endDate) {
        long totalElements = todoMapper.countByUserId(userId, keyword, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int offset = (page - 1) * size;
        List<Todos> todos = todoMapper.findByUserId(userId, offset, size, keyword, startDate, endDate);

        List<TodoResponseDTO> doList = todos.stream()
                .map(TodoResponseDTO::new)
                .collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(page, size, totalElements, totalPages);
        return new PageResponseDTO<>(doList, pageInfo);
    }

    // 투두 상세 조회
    public TodoResponseDTO get(long id, long userId) {
        Todos todos = validateAndGetTodos(id, userId);
        return new TodoResponseDTO(todos);
    }

    // 투두 수정
    public TodoResponseDTO update(long id, long userId, TodoUpdateRequestDTO req) {
        Todos todos = validateAndGetTodos(id, userId);

        if (req.getTitle() != null) {
            todos.setTitle(req.getTitle());
        }

        if (req.getStatus() != null) {
            if (req.getStatus() == TodoStatus.DONE && todos.getStatus() != TodoStatus.DONE) {
                todos.setCompletedAt(LocalDateTime.now());
            }
            todos.setStatus(req.getStatus());
        }

        todoMapper.update(todos);

        return new TodoResponseDTO(todos);
    }

    // 투두 삭제
    public void delete(long id, long userId) {
        validateAndGetTodos(id, userId);
        todoMapper.deleteByIdAndUserId(id, userId);
    }

    private Todos validateAndGetTodos(long id, long userId) {
        Todos todos = todoMapper.findByIdAndUserId(id, userId);
        if (todos == null) {
            throw new TodoNotFoundException("해당 투두 항목을 찾을 수 없거나 접근 권한이 없습니다.");
        }
        return todos;
    }
}
