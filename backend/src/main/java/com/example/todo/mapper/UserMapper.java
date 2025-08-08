package com.example.todo.mapper;

import com.example.todo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByEmail(@Param("email") String email); // @Param 어노테이션은 xml에서 #{email}로 받을 수 있게 이름 고정

    void insert(User user);
}
