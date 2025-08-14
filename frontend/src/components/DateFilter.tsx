import { MaterialIcons } from "@expo/vector-icons";
import React, { useState } from "react";
import { Modal, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Calendar, LocaleConfig } from "react-native-calendars";

LocaleConfig.locales["kr"] = {
  monthNames: [
    "1월",
    "2월",
    "3월",
    "4월",
    "5월",
    "6월",
    "7월",
    "8월",
    "9월",
    "10월",
    "11월",
    "12월",
  ],
  monthNamesShort: [
    "1월",
    "2월",
    "3월",
    "4월",
    "5월",
    "6월",
    "7월",
    "8월",
    "9월",
    "10월",
    "11월",
    "12월",
  ],
  dayNames: [
    "일요일",
    "월요일",
    "화요일",
    "수요일",
    "목요일",
    "금요일",
    "토요일",
  ],
  dayNamesShort: ["일", "월", "화", "수", "목", "금", "토"],
};
LocaleConfig.defaultLocale = "kr";

type DateFileterProps = {
  onApply: (startDate: string | null, endDate: string | null) => void;
};

export default function DateFilter({ onApply }: DateFileterProps) {
  const [modalVisible, setModalVisible] = useState(false);
  const [startDate, setStartDate] = useState<string | null>(null);
  const [endDate, setEndDate] = useState<string | null>(null);

  const handleDayPress = (day: any) => {
    if (!startDate || (startDate && endDate)) {
      setStartDate(day.dateString);
      setEndDate(null);
    } else if (day.dateString >= startDate) {
      setEndDate(day.dateString);
    } else {
      setStartDate(day.dateString);
      setEndDate(null);
    }
  };

  const handleApply = () => {
    onApply(startDate, endDate);
    setModalVisible(false);
  };

  const handleReset = () => {
    setStartDate(null);
    setEndDate(null);
    onApply(null, null);
    setModalVisible(false);
  };

  const getMarkedDates = () => {
    const marked: any = {};

    if (startDate) {
      marked[startDate] = {
        startingDay: true,
        color: "#007BFF",
        textColor: "white",
      };
    }

    if (endDate) {
      marked[endDate] = {
        endingDay: true,
        color: "#007BFF",
        textColor: "white",
      };

      let current = new Date(startDate as string);
      const end = new Date(endDate);
      while (current < end) {
        current.setDate(current.getDate() + 1);
        const dateString = current.toISOString().split("T")[0];
        if (dateString !== startDate && dateString !== endDate) {
          marked[dateString] = { color: "#D4E9FF", textColor: "black" };
        }
      }
    }
    return marked;
  };

  return (
    <View>
      <TouchableOpacity
        style={styles.filterButton}
        onPress={() => setModalVisible(true)}
      >
        <MaterialIcons name="date-range" size={20} color="#555" />
        <Text style={styles.filterButtonText}>날짜 필터</Text>
      </TouchableOpacity>

      <Modal visible={modalVisible} animationType="slide">
        <View style={styles.modalContainer}>
          <Calendar
            onDayPress={handleDayPress}
            markingType={"period"}
            markedDates={getMarkedDates()}
          />
          <View style={styles.buttonContainer}>
            <Button title="초기화" onPress={handleReset} color="#6c757d" />
            <Button title="적용" onPress={handleApply} />
          </View>
        </View>
      </Modal>
    </View>
  );
}

const Button = ({
  title,
  onPress,
  color,
}: {
  title: string;
  onPress: () => void;
  color?: string;
}) => (
  <TouchableOpacity
    style={[styles.modalButton, { backgroundColor: color || "#007BFF" }]}
    onPress={onPress}
  >
    <Text style={styles.modalButtonText}>{title}</Text>
  </TouchableOpacity>
);

const styles = StyleSheet.create({
  filterButton: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "white",
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 20,
    borderColor: "#E0E0E0",
    borderWidth: 1,
  },
  filterButtonText: {
    marginLeft: 5,
    fontSize: 14,
  },
  modalContainer: {
    flex: 1,
    paddingTop: 50,
  },
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-around",
    padding: 20,
  },
  modalButton: {
    paddingVertical: 12,
    paddingHorizontal: 30,
    borderRadius: 8,
  },
  modalButtonText: {
    color: "white",
    fontWeight: "bold",
    fontSize: 16,
  },
});
