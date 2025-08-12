import React, { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Button,
  FlatList,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";
import { useAuth } from "../contexts/AuthContext";
import apiClient from "../api/client";

interface Todo {
  id: number;
  title: string;
  status: "TODO" | "IN_PROGRESS" | "DONE";
}

export default function MainScreen() {
  const { signOut } = useAuth();
  const [todos, setTodos] = useState<Todo[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [newTodoTitle, setNewTodoTitle] = useState("");

  useEffect(() => {
    const fetchTodos = async () => {
      try {
        const response = await apiClient("/api/todos");
        setTodos(response.data.data);
      } catch (error) {
        console.error("할 일 목록을 불러오는데 실패했습니다.", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchTodos();
  }, []);

  const handleAddTodo = async () => {
    if (!newTodoTitle.trim()) {
      Alert.alert("오류", "할 일 내용을 입력해주세요.");
      return;
    }
    try {
      const response = await apiClient("/api/todos", {
        method: "POST",
        body: JSON.stringify({ title: newTodoTitle }),
      });

      setTodos([response.data, ...todos]);
      setNewTodoTitle("");
    } catch (error) {
      console.error("할 일 추가에 실패했습니다.", error);
      Alert.alert("오류", "할 일 추가에 실패했습니다.");
    }
  };

  if (isLoading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>나의 할 일 목록</Text>
      <View style={styles.inputContainer}>
        <TextInput
          style={styles.input}
          placeholder="새로운 할 일을 입력하세요..."
          value={newTodoTitle}
          onChangeText={setNewTodoTitle}
        />
        <Button title="추가" onPress={handleAddTodo}/>
      </View>
      <FlatList
        data={todos}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.todoItem}>
            <Text style={styles.todoTitle}>{item.title}</Text>
            <Text>상태: {item.status}</Text>
          </View>
        )}
        ListEmptyComponent={<Text>할 일이 없습니다. 추가해주세요!</Text>}
      />
      <Button title="로그아웃" onPress={signOut} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  centered: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 20,
    marginTop: 20, // inputContainer와의 간격
    textAlign: "center",
  },
  inputContainer: {
    flexDirection: "row", // 아이템을 가로로 배치
    marginBottom: 20,
  },
  input: {
    flex: 1, // 남은 공간을 모두 차지
    height: 40,
    borderColor: "#CCCCCC",
    borderWidth: 1,
    borderRadius: 8,
    paddingHorizontal: 10,
    marginRight: 10, // 버튼과의 간격
  },
  todoItem: {
    backgroundColor: "#f9f9f9",
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
  },
  todoTitle: {
    fontSize: 18,
    fontWeight: "500",
  },
});
