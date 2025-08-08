package com.example.todo.service;

import com.example.todo.domain.User;
import com.example.todo.dto.request.LoginRequestDTO;
import com.example.todo.dto.request.RegisterRequestDTO;
import com.example.todo.dto.response.TokenResponseDTO;
import com.example.todo.dto.response.UserResponseDTO;
import com.example.todo.mapper.UserMapper;
import com.example.todo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO req) {
        // 이메일 중복체크
        User existed = userMapper.findByEmail(req.getEmail());
        if (existed != null) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 저장
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userMapper.insert(user);

        return new UserResponseDTO(user.getId(), user.getEmail());
    }

    public TokenResponseDTO login(LoginRequestDTO req) {
        User user = userMapper.findByEmail(req.getEmail());
        // 로그인 검증
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        
        // 토큰 생성
        String token = jwtTokenProvider.generateToken(req.getEmail());
        // 토큰 응답 반환
        return new TokenResponseDTO(token, "Bearer", 3600000L);
    }

}
