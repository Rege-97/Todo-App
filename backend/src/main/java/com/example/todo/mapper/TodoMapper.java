package com.example.todo.mapper;

import com.example.todo.domain.Todos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TodoMapper {

    void insert(Todos todos);

    List<Todos> findByUserId(@Param("userId") Long userId,
                             @Param("offset") int offset,
                             @Param("size") int size,
                             @Param("keyword") String keyword,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    Todos findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    void update(Todos todos);

    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    long countByUserId(@Param("userId") Long userId,
                       @Param("keyword") String keyword,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate);

}
