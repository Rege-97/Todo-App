package com.example.todo.service;

import com.example.todo.domain.User;
import com.example.todo.dto.request.LoginRequestDTO;
import com.example.todo.dto.request.RegisterRequestDTO;
import com.example.todo.mapper.UserMapper;
import com.example.todo.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// JUnit 5와 Mockito를 함께 사용하기 위한 설정
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    // 테스트 대상이 되는 클래스
    @InjectMocks
    private UserService userService;

    // 가짜 객체, 동작은 하지 않음
    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    // 실제 객체, 실제로 필요한 경우에만 Mock이 아닌 Spy로 생성
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일")
    void register_fail_when_email_is_duplicated() {
        // given 주어진 상황
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setEmail("email@email.com");
        registerRequestDTO.setPassword("password");

        // when $ then 무엇을 할 때 어떤 결과가 나와야하는가
        // userMapper.findByEmail() 메서드가 "email@email.com"로 호출될 때
        // 가짜 User객체를 반환
        when(userMapper.findByEmail("email@email.com")).thenReturn(new User());

        // assertThrows 특정 예외가 발생하는지 검증
        // userService.register()를 호출했을 때 IllegalArgumentException이 발생하는지 검증
        assertThrows(IllegalArgumentException.class, () -> userService.register(registerRequestDTO));

        // verify 특정 메서드가 몇번 호출되었는지 검증
        // userMapper.insert() 메소드가 한번도 호출되지 않았는지 검증
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        // given
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setEmail("email@email.com");
        registerRequestDTO.setPassword("password");

        // 어떤 문자열이든 파라미터로 받아서 호출되면 null 반환해라
        when(userMapper.findByEmail(anyString())).thenReturn(null);
        // doNoting() 반환값이 void 인 메서드의 행동을 정의할 때 사용(아무것도 하지마라)
        // userMapper의 insert가 어떤 User클래스를 받아서 호출되든 아무것도 하지말아라
        doNothing().when(userMapper).insert(any(User.class));

        // when
        userService.register(registerRequestDTO);

        // then
        // passwordEncoder 객체의 encode라는 메서드가 "password"라는 매개변수로 정확히 1번 호출되었는지 검증
        verify(passwordEncoder, times(1)).encode("password");
        // userMapper 객체의 insert함수가 어떤 User객체든 매개변수로 왔을 때 정확히 1번 호출 되었는지 검증
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        String email = "email@email.com";

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("email@email.com");
        loginRequestDTO.setPassword("password");

        User fakeUser = new User();
        fakeUser.setEmail(email);
        fakeUser.setPassword(encodedPassword);

        when(userMapper.findByEmail(email)).thenReturn(fakeUser);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtTokenProvider.generateToken(fakeUser.getId(), email)).thenReturn("token");

        // when
        userService.login(loginRequestDTO);

        // then
        verify(userMapper, times(1)).findByEmail(loginRequestDTO.getEmail());
        verify(jwtTokenProvider, times(1)).generateToken(fakeUser.getId(), loginRequestDTO.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_with_wrong_password() {
        // given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("email@email.com");
        loginRequestDTO.setPassword("password");

        when(userMapper.findByEmail(loginRequestDTO.getEmail())).thenReturn(new User());

        // when
        assertThrows(IllegalArgumentException.class, () -> userService.login(loginRequestDTO));

        // then
        verify(jwtTokenProvider, never()).generateToken(anyLong(), loginRequestDTO.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_fail_with_wrong_email() {
        // given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("email@email.com");
        loginRequestDTO.setPassword("password");

        when(userMapper.findByEmail(loginRequestDTO.getEmail())).thenReturn(null);

        // when
        assertThrows(IllegalArgumentException.class, () -> userService.login(loginRequestDTO));

        // then
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenProvider, never()).generateToken(anyLong(), loginRequestDTO.getEmail());
    }

}
