package com.example.todo.service;

import com.example.todo.domain.TodoStatus;
import com.example.todo.domain.Todos;
import com.example.todo.dto.response.TodoResponseDTO;
import com.example.todo.mapper.TodoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoMapper todoMapper;

    @Test
    @DisplayName("TODO 생성 성공")
    void todo_create_success() {
        // given
        long userId = 1L;
        String title = "test";

        // TodoMapper.insert()가 호출될 때 아무것도 하지 않도록 설정
        doNothing().when(todoMapper).insert(any(Todos.class));

        // when
        TodoResponseDTO result = todoService.create(userId, title);

        // then
        // 반환된 DTO의 내용이 예상과 일치하는지 확인
        assertEquals(title, result.getTitle());
        assertEquals(TodoStatus.TODO.name(), result.getStatus());

        // 메서드가 정확히 1번만 호출되었는지 확인
        verify(todoMapper, times(1)).insert(any(Todos.class));

        // 매퍼에 전달된 값들이 제대로 들어갔는지 확인
        // ArgumentCaptor: Mockito 메서드에 전달된 실제 인자를 캡처하는 도구
        ArgumentCaptor<Todos> todosCaptor = ArgumentCaptor.forClass(Todos.class);
        verify(todoMapper).insert(todosCaptor.capture());

        Todos capturedTodos = todosCaptor.getValue();
        assertEquals(userId, capturedTodos.getUserId());
        assertEquals(title, capturedTodos.getTitle());
        assertEquals(TodoStatus.TODO, capturedTodos.getStatus());
    }


}
