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
};

export default function TodoItem({ item, onToggle, onDelete }: TodoItemProps) {
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
      <TouchableOpacity onPress={() => onDelete(item.id)}>
        <MaterialIcons name="delete" size={24} color="#FF3B30" />
      </TouchableOpacity>
    </TouchableOpacity>
  );
}
const styles = StyleSheet.create({
  container: {
    backgroundColor: "#fff",
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    elevation: 2, // Android 그림자
    shadowColor: "#000", // iOS 그림자
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.2,
    shadowRadius: 1.41,
  },
  leftContainer: {
    flexDirection: "row",
    alignItems: "center",
    flex: 1,
  },
  title: {
    fontSize: 16,
    marginLeft: 10,
  },
  completed: {
    textDecorationLine: "line-through",
    color: "#888",
  },
});
