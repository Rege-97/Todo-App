import React from "react";
import { Button, StyleSheet, Text, View } from "react-native";
import { useAuth } from "../contexts/AuthContext";

export default function MainScreen() {
  const { signOut } = useAuth();

  return (
    <View style={styles.container}>
      <Text>로그인 성공! 여기는 메인화면입니다.</Text>
      <Button title="로그아웃" onPress={signOut} />
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
