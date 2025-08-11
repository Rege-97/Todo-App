import { NativeStackScreenProps } from "@react-navigation/native-stack";
import React, { useState } from "react";
import { Alert, Button, StyleSheet, Text, TextInput, View } from "react-native";
import { RootStackParamList } from "../../App";

const API_URL = "http://172.30.1.55:8080";

type LoginScreenProps = NativeStackScreenProps<RootStackParamList, "Login">;

export default function LoginScreen({ navigation }: LoginScreenProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert("오류", "이메일과 비밀번호를 모두 입력해주세요.");
      return;
    }
    try {
      const response = await fetch(`${API_URL}/api/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: email,
          password: password,
        }),
      });

      if (!response.ok) {
        throw new Error("로그인에 실패했습니다.");
      }

      const data = await response.json();

      console.log("로그인 성공! 서버 응답: ", data);
      Alert.alert("성공", "로그인에 성공했습니다!");

      // TODO: 여기에 토큰 저장 로직 추가 예정

      navigation.navigate("Main");
    } catch (error) {
      console.log("로그인 실패:", error);
      Alert.alert(
        "오류",
        "로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요."
      );
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>로그인</Text>
      <TextInput
        style={styles.input}
        placeholder="이메일을 입력하세요"
        value={email}
        onChangeText={setEmail}
        keyboardType="email-address"
        autoCapitalize="none"
      />
      <TextInput
        style={styles.input}
        placeholder="비밀번호를 입력하세요"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />
      <Button title="로그인하기" onPress={handleLogin} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    padding: 20, // 전체적인 여백
  },
  title: {
    fontSize: 32,
    fontWeight: "bold",
    textAlign: "center",
    marginBottom: 40,
  },
  input: {
    height: 50,
    borderColor: "#CCCCCC",
    borderWidth: 1,
    borderRadius: 8,
    paddingHorizontal: 15,
    fontSize: 16,
    marginBottom: 15, // 입력창 사이의 간격
  },
});
