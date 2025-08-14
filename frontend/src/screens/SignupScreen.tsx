import { NativeStackScreenProps } from "@react-navigation/native-stack";
import React, { useState } from "react";
import { RootStackParamList } from "../../App";
import {
  Alert,
  Button,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import apiClient from "../api/client";

type SignupScreenProps = NativeStackScreenProps<RootStackParamList, "Signup">;

export default function SignupScreen({ navigation }: SignupScreenProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleSignup = async () => {
    if (!email || !password || !confirmPassword) {
      return Alert.alert("오류", "모든 필드를 입력해주세요.");
    }
    if (password !== confirmPassword) {
      return Alert.alert("오류", "비밀번호가 일치하지 않습니다,");
    }

    try {
      await apiClient("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });

      Alert.alert(
        "성공",
        "회원가입에 성공했습니다! 로그인 화면으로 이동합니다.",
        [{ text: "확인", onPress: () => navigation.navigate("Login") }]
      );
    } catch (error: any) {
      console.error("회원가입 실패:", error);
      Alert.alert("오류", error.message || "회원가입 중 문제가 발생했습니다.");
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>회원가입</Text>
      <TextInput
        style={styles.input}
        placeholder="이메일을 입력하세요."
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
      <TextInput
        style={styles.input}
        placeholder="비밀번호를 다시 입력하세요"
        value={confirmPassword}
        onChangeText={setConfirmPassword}
        secureTextEntry
      />
      <Button title="가입하기" onPress={handleSignup} />
      <TouchableOpacity onPress={() => navigation.navigate("Login")}>
        <Text style={styles.linkText}>이미 계정이 있으신가요? 로그인</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    padding: 20,
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
    marginBottom: 15,
  },
  linkText: {
    color: "#007BFF",
    textAlign: "center",
    marginTop: 20,
  },
});
