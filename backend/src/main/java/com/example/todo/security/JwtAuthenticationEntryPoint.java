package com.example.todo.security;

import com.example.todo.common.ApiResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// AuthenticationEntryPoint: 인증이 필요한 요청인데 인증이 실패/누락됐을 때 무엇을 응답할지 결정하는 보안 훅(콜백) 인터페이스
// 공통 응답을 하기 위해 상속받아 오버라이딩
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper(); // JSON 직렬화를 위해 Jackson ObjectMapper

    // 스프링 시큐리티가 자동 호출하는 메서드
    // 인증 실패가 감지되면 이 메서드가 실행되어 직접 응답 바디 제작
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401
        response.setContentType("application/json;charset=UTF-8");

        ApiResponseDTO<Object> body = ApiResponseDTO.error(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다. 유효한 토큰을 포함해주세요.");
        response.getWriter().write(mapper.writeValueAsString(body));    // JSON 문자열로 바꿔 응답 바디에 씀
    }
}
