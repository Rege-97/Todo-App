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
import { MaterialIcons } from "@expo/vector-icons";
import TodoModal from "../components/TodoModal";

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
  const [modalVisible, setModalVisible] = useState(false);
  const [editingTodo, setEditingTodo] = useState<Todo | null>(null);

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [isFetchingMore, setIsFetchMore] = useState(false);

  const dedupeById = (list: Todo[]) => {
    const map = new Map<number, Todo>();
    for (const t of list) map.set(t.id, t);
    return Array.from(map.values());
  };

  const fetchTodos = async (page: number) => {
    if (isFetchingMore) {
      return;
    }

    if (page === 1) {
      setIsLoading(true);
    } else {
      setIsFetchMore(true);
    }

    try {
      const response = await apiClient(`/api/todos?page=${page}&size=10`);
      const fetchTodos = response.data.data;
      const pageInfo = response.data.pageInfo;

      if (page === 1) {
        setTodos(dedupeById(fetchTodos));
      } else {
        setTodos((prevTodos) => dedupeById([...prevTodos, ...fetchTodos]));
      }
      setTotalPages(pageInfo.totalPages);
      setCurrentPage(page);
    } catch (error) {
      console.error("할 일 목록을 불러오는데 실패했습니다.", error);
    } finally {
      setIsLoading(false);
      setIsFetchMore(false);
    }
  };

  useEffect(() => {
    fetchTodos(1);
  }, []);

  const handleLoadMore = () => {
    if (currentPage < totalPages && !isFetchingMore) {
      const nextPage = currentPage + 1;
      setCurrentPage(nextPage);
      fetchTodos(nextPage);
    }
  };

  const handleAddTodo = async (title: string) => {
    try {
      const response = await apiClient("/api/todos", {
        method: "POST",
        body: JSON.stringify({ title }),
      });

      setTodos([response.data, ...todos]);
      setModalVisible(false);
    } catch (error) {
      console.error("할 일 추가 실패:", error);
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

  const handleUpdateTodo = async (title: string) => {
    if (!editingTodo) {
      return;
    }
    try {
      const response = await apiClient(`/api/todos/${editingTodo.id}`, {
        method: "PUT",
        body: JSON.stringify({ title }),
      });
      setTodos((prevTodos) =>
        prevTodos.map((todo) =>
          todo.id === editingTodo.id ? response.data : todo
        )
      );
      closeModal();
    } catch (error) {
      console.error("할 일 수정 실패: ", error);
      Alert.alert("오류", "할 일 수정에 실패했습니다.");
    }
  };

  const handleOpenEditModal = (todo: Todo) => {
    setEditingTodo(todo);
    setModalVisible(true);
  };

  const handleOpenAddModal = () => {
    setEditingTodo(null);
    setModalVisible(true);
  };

  const closeModal = () => {
    setModalVisible(false);
    setEditingTodo(null);
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

  const renderFooter = () => {
    if (!isFetchingMore) {
      return null;
    }
    return <ActivityIndicator style={{ marginVertical: 20 }} />;
  };

  if (isLoading) {
    return (
      <View>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>나의 할 일 목록</Text>
        <Button title="로그아웃" onPress={signOut} />
      </View>

      <FlatList
        data={todos}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <TodoItem
            item={item}
            onToggle={handleToggleTodo}
            onDelete={handleDeleteTodo}
            onEdit={handleOpenEditModal}
          />
        )}
        ListEmptyComponent={
          <Text style={styles.emptyText}>할 일이 없습니다. 추가해주세요!</Text>
        }
        onEndReached={handleLoadMore}
        onEndReachedThreshold={0.5}
        ListFooterComponent={renderFooter}
      />
      <TouchableOpacity
        style={styles.fab}
        onPress={() => setModalVisible(true)}
      >
        <MaterialIcons name="add" size={24} color="white" />
      </TouchableOpacity>
      <TodoModal
        visible={modalVisible}
        onClose={closeModal}
        onSubmit={editingTodo ? handleUpdateTodo : handleAddTodo}
        initialValue={editingTodo ? editingTodo.title : ""}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F5F5F5",
  },
  header: {
    padding: 15,
    paddingTop: 40, // 상태바 영역 확보
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    backgroundColor: "white",
  },
  title: {
    fontSize: 28,
    fontWeight: "bold",
  },
  emptyText: {
    textAlign: "center",
    marginTop: 50,
    fontSize: 16,
    color: "#888",
  },
  fab: {
    position: "absolute", // 화면 위에 떠 있도록
    right: 20,
    bottom: 20,
    backgroundColor: "#007BFF",
    width: 60,
    height: 60,
    borderRadius: 30, // 원형으로 만들기
    justifyContent: "center",
    alignItems: "center",
    elevation: 8, // 그림자 효과
  },
});
