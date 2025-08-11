package com.example.todo.service;

import com.example.todo.domain.RefreshToken;
import com.example.todo.domain.User;
import com.example.todo.dto.request.LoginRequestDTO;
import com.example.todo.dto.request.RegisterRequestDTO;
import com.example.todo.dto.request.TokenRefreshRequestDTO;
import com.example.todo.dto.response.TokenResponseDTO;
import com.example.todo.dto.response.UserResponseDTO;
import com.example.todo.exception.DuplicateEmailException;
import com.example.todo.mapper.RefreshTokenMapper;
import com.example.todo.mapper.UserMapper;
import com.example.todo.security.CustomUserDetails;
import com.example.todo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {    // 인증 검증을 위해 UserDetailsService 인터페이스 상속

    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO req) {
        // 이메일 중복체크
        User existed = userMapper.findByEmail(req.getEmail());
        if (existed != null) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        // 저장
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userMapper.insert(user);

        return new UserResponseDTO(user.getId(), user.getEmail(), user.getCreatedAt());
    }

    // 로그인
    public TokenResponseDTO login(LoginRequestDTO req) {
        // 사용자 인증
        User user = userMapper.findByEmail(req.getEmail());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 액세스 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToekn(user.getId(), user.getEmail());
        // 리프레쉬 토큰 생성
        String refreshTokenString = jwtTokenProvider.generateRefreshToken(user.getId());
        // 리프레쉬 토큰 만료일 계산
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000);

        // 리프레쉬 객체 생성 및 세팅
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(refreshTokenString);
        refreshToken.setExpiryDate(expiresAt);

        // 기존에 리프레쉬 토큰이 있는지 확인
        Optional<RefreshToken> existingToken = refreshTokenMapper.findByUserId(user.getId());
        if (existingToken.isPresent()) {
            refreshTokenMapper.update(refreshToken);    // 있으면 업데이트
        } else {
            refreshTokenMapper.save(refreshToken);      // 없으면 저장
        }

        // 투 토큰을 모아서 반환
        return new TokenResponseDTO(accessToken, refreshTokenString, "Bearer", 3600000L);
    }

    // 액세스 토큰 재발급 메서드
    @Transactional
    public TokenResponseDTO refreshAccessToken(TokenRefreshRequestDTO req) {
        String refreshTokenString = req.getRefreshToken();

        // DB에서 리프레쉬 토큰 조회
        RefreshToken refreshToken = refreshTokenMapper.findByToken(refreshTokenString)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레쉬 토큰입니다."));

        // 리프레쉬 토큰 만료시간 검증
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenMapper.deleteByUserId(refreshToken.getUserId());
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        // 리프레쉬 토큰의 사용자 정보 조회
        User user = userMapper.findById(refreshToken.getId())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToekn(user.getId(), user.getEmail());

        // 반환
        return new TokenResponseDTO(newAccessToken, refreshTokenString, "Bearer", 3600000L);
    }

    // 회원 디테일 정보 메서드
    // 직접 호출하지 않고 스프링 시큐리티가 자동으로 인증이 필요할 때 메서드 호출
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        }

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),    // username
                user.getPassword(), // password
                Collections.emptyList() // role(권한)
        );
    }
}
