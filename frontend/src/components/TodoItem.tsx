import { MaterialIcons } from "@expo/vector-icons";
import React from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";

type TodoStatus = "TODO" | "IN_PROGRESS" | "DONE";

interface Todo {
  id: number;
  title: string;
  status: TodoStatus;
}

type MaterialIconName = React.ComponentProps<typeof MaterialIcons>["name"];

type TodoItemProps = {
  item: Todo;
  onToggle: (id: number, status: TodoStatus) => void;
  onDelete: (id: number) => void;
  onEdit: (todo: Todo) => void;
};

export default function TodoItem({
  item,
  onToggle,
  onDelete,
  onEdit,
}: TodoItemProps) {
  const getStatusStyle = (
    status: TodoStatus
  ): { icon: MaterialIconName; color: string } => {
    switch (status) {
      case "TODO":
        return { icon: "radio-button-unchecked", color: "#888" };
      case "IN_PROGRESS":
        return { icon: "incomplete-circle", color: "#007BFF" };
      case "DONE":
        return { icon: "check-circle", color: "#28A745" };
    }
  };

  const statusStyle = getStatusStyle(item.status);

  return (
    <TouchableOpacity
      style={styles.container}
      onPress={() => onToggle(item.id, item.status)}
    >
      <View style={styles.leftContainer}>
        <MaterialIcons
          name={statusStyle.icon}
          size={24}
          color={statusStyle.color}
        />
        <Text
          style={[styles.title, item.status === "DONE" && styles.completed]}
        >
          {item.title}
        </Text>
      </View>
      <View style={styles.buttonsContainer}>
        <TouchableOpacity
          style={styles.iconButton}
          onPress={() => onEdit(item)}
        >
          <MaterialIcons name="edit" size={22} color="#555" />
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.iconButton}
          onPress={() => onDelete(item.id)}
        >
          <MaterialIcons name="delete" size={22} color="#FF3B30" />
        </TouchableOpacity>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#fff",
    paddingVertical: 12,
    paddingHorizontal: 15,
    borderRadius: 8,
    marginBottom: 10,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.2,
    shadowRadius: 1.41,
  },
  leftContainer: {
    flexDirection: "row",
    alignItems: "center",
    flex: 1, // 남은 공간을 차지
    marginRight: 10, // 버튼과의 간격
  },
  title: {
    fontSize: 16,
    marginLeft: 10,
  },
  completed: {
    textDecorationLine: "line-through",
    color: "#888",
  },
  buttonsContainer: {
    flexDirection: "row",
  },
  iconButton: {
    padding: 5,
    marginLeft: 10,
  },
});
