package com.example.todo.mapper;

import com.example.todo.domain.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {
    // 토큰 값으로 리프레쉬 토큰 정보 찾기
    Optional<RefreshToken> findByToken(@Param("token") String token);

    // 사용자 ID로 리프레쉬 토큰 정보 찾기
    Optional<RefreshToken> findByUserId(@Param("userId") long userId);

    // 새로운 리프레쉬 토큰 저장
    void save(RefreshToken refreshToken);

    // 기존 리프레쉬 토큰 업데이트
    void update(RefreshToken refreshToken);
    
    // 사용자 ID로 리프레쉬 토큰 삭제
    void deleteByUserId(@Param("userId") long userId);
}
