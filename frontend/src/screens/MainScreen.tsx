import React from "react";
import { StyleSheet, Text, View } from "react-native";

export default function MainScreen() {
  return (
    <View style={styles.container}>
      <Text>로그인 성공! 여기는 메인화면입니다.</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
});
