package com.example.todo.config;

import com.example.todo.security.JwtAuthenticationEntryPoint;
import com.example.todo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // 스프링 시큐리티에서 요청이 들어올 때 거치는 보안 필터 묶음
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // CSRF 공격방지 기능 비활성화(JWT 기반이므로)
                .cors(Customizer.withDefaults())    // CORS 정책 허용 기능 활성화(규칙은 아래 메서드에서 정의)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))    // 커스텀 인증 예외처리 등록
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // 세션 비저장 설정(JWT로 할것이기 때문)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)   // JWT 필터 추가 (기본 로그인 필터 실행 전 JWT 필터 실행)
                .authorizeHttpRequests(auth -> auth //URL별 접근 권한 설정
                        .requestMatchers("/health", "/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()   // 나머지는 인증 필요
                );
        return http.build();
    }

    // 브라우저에서 다른 출처로 요청 시 허용할지 여부 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();

        // 허용 목록
        conf.setAllowedOrigins(List.of(
                "http://localhost:5173", // 예: Vite 프론트엔드
                "http://localhost:19006" // 예: Expo React Native
        ));
        conf.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        conf.setAllowedHeaders(List.of("*"));   // 허용할 요청 헤더
        conf.setAllowCredentials(true); // 쿠키나 인증 헤더를 포함한 요청 허용 여부

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // URL 패턴별로 서로 다른 CORS 설정을 적용할 수 있게 해주는 객체
        source.registerCorsConfiguration("/**", conf);  // registerCorsConfiguration(URL 패턴, CORS 설정 객체)
        return source; // 스프링 시큐리티가 CORS 규칙을 전역으로 적용
    }
}
