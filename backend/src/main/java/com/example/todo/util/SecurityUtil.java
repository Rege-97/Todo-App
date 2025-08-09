package com.example.todo.util;

import com.example.todo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserMapper userMapper;

    // Optional은 값이 있을 수도 있고 없을 수도 있는 상황에서 null 예외 방지를 위해 사용

    // 로그인된 유저 이메일 반환 메서드
    public static Optional<String> getCurrentUserEmail() {
        // principal은 시큐리티에서 인증된 사용자를 나타내는 객체
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            // principal이 UserDetails 타입이면, 사용자 이메일을 Optional 객체에 담아 반환
            return Optional.of(((UserDetails)principal).getUsername());
        }
        // principal이 UserDetails가 아니면, 빈 Optional 객체를 반환
        return Optional.empty();
    }

    // 로그인된 유저 아이디 반환 메서드
    public long getCurrentUserId() {
        //  orElseThrow() 메서드:
        //    - Optional에 값이 있으면 (사용자 이메일이 있으면) 그 값을 반환
        //    - Optional이 비어 있으면 (사용자 이메일이 없으면) 괄호 안의 람다식이 실행되어 IllegalStateException 예외를 발생
        //    - 이 덕분에 email 변수에는 절대 null이 들어갈 수 없음

        String email = getCurrentUserEmail().orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."));

        // 안전하게 이메일로 사용자 ID를 조회
        return userMapper.findByEmail(email).getId();
    }
}
