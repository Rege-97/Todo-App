package com.example.todo.common.handler;

import com.example.todo.domain.TodoStatus;
import com.example.todo.domain.Todos;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 마이바티스에 ENUM 클래스 맵핑 타입핸들러
@MappedTypes(TodoStatus.class)
public class TodoStatusTypeHandler extends BaseTypeHandler<TodoStatus> {

    // Java(Enum) -> DB(String)로 변환
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TodoStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    // DB(String) -> Java(Enum)로 변환 (컬럼 이름 사용)
    @Override
    public TodoStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String status = rs.getString(columnName);
        return status == null ? null : TodoStatus.valueOf(status);
    }

    // DB(String) -> Java(Enum)로 변환 (컬럼 인덱스 사용)
    @Override
    public TodoStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String status = rs.getString(columnIndex);
        return status == null ? null : TodoStatus.valueOf(status);
    }

    // DB(String) -> Java(Enum)로 변환 (프로시저 호출 결과)
    @Override
    public TodoStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String status = cs.getString(columnIndex);
        return status == null ? null : TodoStatus.valueOf(status);
    }
}
