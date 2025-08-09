package com.example.todo.util;

import com.example.todo.mapper.UserMapper;
import com.example.todo.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    // Optional은 값이 있을 수도 있고 없을 수도 있는 상황에서 null 예외 방지를 위해 사용

    // 로그인된 유저 이메일 반환 메서드
    public static Optional<String> getCurrentUserEmail() {
        // principal은 시큐리티에서 인증된 사용자를 나타내는 객체
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            // principal이 UserDetails 타입이면, 사용자 이메일을 Optional 객체에 담아 반환
            return Optional.of(((UserDetails) principal).getUsername());
        }
        // principal이 UserDetails가 아니면, 빈 Optional 객체를 반환
        return Optional.empty();
    }

    // 로그인된 유저 아이디 반환 메서드
    public static long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {   // id 정보는 커스텀한 유저 디테일에 있으므로 커스텀 유저 디테일과 비교
            return ((CustomUserDetails) principal).getId();
        }
        throw new IllegalStateException("인증된 사용자를 찾을 수 없거나 사용자 ID가 포함되어 있지 않습니다.");
    }
}
