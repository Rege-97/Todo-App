package com.example.todo.service;

import com.example.todo.domain.TodoStatus;
import com.example.todo.domain.Todos;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoMapper todoMapper;

    // 투두 생성
    public Todos create(long userId, String title) {
        Todos todos = new Todos();
        todos.setUserId(userId);
        todos.setTitle(title);
        todos.setStatus(TodoStatus.TODO);
        todoMapper.insert(todos);
        return todos;
    }

    // 투두 목록 조회
    public List<Todos> getList(long userId) {
        return todoMapper.findByUserId(userId);
    }

    // 투두 상세 조회
    public Todos get(long id, long userId) {
        return validateAndGetTodos(id, userId);
    }

    // 투두 수정
    public Todos update(long id, long userId, String title, TodoStatus status) {
        Todos todos = validateAndGetTodos(id, userId);

        if (title != null) {
            todos.setTitle(title);
        }

        if (status != null) {
            todos.setStatus(status);
        }

        todoMapper.update(todos);

        return todos;
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
