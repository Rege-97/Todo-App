package com.example.todo.security;

import com.example.todo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
//@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 모든 요청에 대해 한번만 실행되는 필터를 만들 때 사용
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    // 순환참조 문제를 막기 위해 생성자로 @Lazy 사용
    // @Lazy는 객체 생성을 지연시키고 필요할 때 생성
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, @Lazy UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);  // 요청에서 Authorization 헤더에서 토큰 추출

            if (token != null && jwtTokenProvider.getSubject(token) != null) {    // 토큰 유효성 검사
                String email = jwtTokenProvider.getSubject(token);

                UserDetails userDetails = userService.loadUserByUsername(email);    // 사용자 정보 로드
                // 인증 객체 생성
                // 이 객체는 Spring Security가 내부적으로 사용자를 인증된 상태로 관리하기 위해 필요
                // 누구, 증명수단, 권한 등록(JWT는 이미 토큰으로 증명이 되어있으므로 증명수단 null)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContextHolder에 인증 정보 설정
                // 이렇게 하면 해당 요청을 처리하는 동안 사용자가 인증된 상태가 됨
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 서명 불일치 등은 그냥 인증 미설정으로 두고 다음 필터로
            log.warn("Could not set user authentication in security context: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 "Bearer " 토큰을 파싱하는 메서드
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
