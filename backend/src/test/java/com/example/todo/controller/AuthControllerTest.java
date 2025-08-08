package com.example.todo.controller;

import com.example.todo.dto.request.RegisterRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest // 통합 테스트 어노테이션
@AutoConfigureMockMvc   // MockMvc 객체를 자동으로 설정하고 주입
@Transactional  // 데이터베이스 롤백
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;    // API를 테스트하기 위한 가짜 MVC 객체

    @Autowired
    private ObjectMapper objectMapper;  // Java 객체를 JSON 문자열로 변환

    @Test
    @DisplayName("회원가입 API 통합 테스트 - 성공")
    void register_api_test_success() throws Exception {
        // given
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setEmail("email@email.com");
        registerRequestDTO.setPassword("password");

        // DTO 객체를 JSON 문자열로 변환
        String jsonRequest = objectMapper.writeValueAsString(registerRequestDTO);

        // when & then (API를 호출했을 때 어떤 결과가 나와야 하는가)
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)   // API 호출
        )
                .andExpect(status().isCreated())    // andExpect(검증)
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("회원가입 성공"))
                .andExpect(jsonPath("$.data.email").value(registerRequestDTO.getEmail()))
                .andDo(print()); // 검증 후 상세내용 출력
    }
    
    @Test
    @DisplayName("회원가입 API 통합 테스트 - 실패(잘못된 입력 값)")
    void register_api_test_fail_with_invalid_input() throws Exception {
        // given
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setEmail("email");
        registerRequestDTO.setPassword("1234");
        
        String jsonRequest = objectMapper.writeValueAsString(registerRequestDTO);

        // when & then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message",containsString("email")))   // 문자열 포함 여부
                .andExpect(jsonPath("$.message",containsString("password")))
                .andDo(print());
    }
}
