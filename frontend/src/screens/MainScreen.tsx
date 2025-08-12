import React, { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Button,
  FlatList,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import { useAuth } from "../contexts/AuthContext";
import apiClient from "../api/client";
import TodoItem from "../components/TodoItem";

type TodoStatus = "TODO" | "IN_PROGRESS" | "DONE";

interface Todo {
  id: number;
  title: string;
  status: TodoStatus;
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

  const handleDeleteTodo = (id: number) => {
    Alert.alert("삭제 확인", "정말로 이 할일을 삭제하시겠습니까?", [
      { text: "아니오", style: "cancel" },
      {
        text: "삭제",
        onPress: async () => {
          try {
            await apiClient(`/api/todos/${id}`, {
              method: "DELETE",
            });
            setTodos((prevTodos) => prevTodos.filter((todo) => todo.id !== id));
          } catch (error) {
            console.log("할 일 삭제에 실패했습니다.");
            Alert.alert("오류", "할 일 삭제에 실패했습니다.");
          }
        },
        style: "destructive",
      },
    ]);
  };

  const handleToggleTodo = async (id: number, currentStatus: TodoStatus) => {
    let newStatus: TodoStatus;
    if (currentStatus === "TODO") {
      newStatus = "IN_PROGRESS";
    } else if (currentStatus === "IN_PROGRESS") {
      newStatus = "DONE";
    } else {
      newStatus = "TODO";
    }

    try {
      setTodos((prevTodos) =>
        prevTodos.map((todo) =>
          todo.id === id ? { ...todo, status: newStatus } : todo
        )
      );

      await apiClient(`/api/todos/${id}`, {
        method: "PUT",
        body: JSON.stringify({ status: newStatus }),
      });
    } catch (error) {
      console.error("할 일 상태 변경에 실패했습니다.", error);
      Alert.alert("오류", "상태 변경에 실패했습니다.");

      setTodos((prevTodos) =>
        prevTodos.map((todo) =>
          todo.id === id ? { ...todo, status: currentStatus } : todo
        )
      );
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
        <Button title="추가" onPress={handleAddTodo} />
      </View>

      <FlatList
        data={todos}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <TodoItem
            item={item}
            onToggle={handleToggleTodo}
            onDelete={handleDeleteTodo}
          />
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
    backgroundColor: "#F5F5F5", // 배경색 변경
    padding: 15,
  },
  centered: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    fontSize: 28,
    fontWeight: "bold",
    marginBottom: 20,
    marginTop: 10,
    textAlign: "center",
  },
  inputContainer: {
    flexDirection: "row",
    marginBottom: 15,
  },
  input: {
    flex: 1,
    backgroundColor: "white",
    height: 45,
    borderColor: "#E0E0E0",
    borderWidth: 1,
    borderRadius: 8,
    paddingHorizontal: 15,
    marginRight: 10,
    fontSize: 16,
  },
  emptyText: {
    textAlign: "center",
    marginTop: 50,
    fontSize: 16,
    color: "#888",
  },
});
