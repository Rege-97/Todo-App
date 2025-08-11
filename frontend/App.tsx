import React, { useEffect, useState } from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import LoginScreen from "./src/screens/LoginScreen";
import MainScreen from "./src/screens/MainScreen";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { ActivityIndicator, Text, View } from "react-native";

export type RootStackParamList = {
  Login: undefined;
  Main: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function App() {
  const [isLoading, setIsLoading] = useState(true);
  const [userToken, setUserToken] = useState<string | null>(null);

  useEffect(() => {
    const checkToken = async () => {
      try {
        const token = await AsyncStorage.getItem("accessTokken");
        setUserToken(token);
      } catch (error) {
        console.log("토큰 확인 중 에러 발생", error);
      } finally {
        setIsLoading(false);
      }
    };

    checkToken();
  }, []);

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <ActivityIndicator size="large" />
        <Text>앱을 로딩하고 있습니다...</Text>
      </View>
    );
  }

  return (
    <NavigationContainer>
      {/**브라우저 라우터 처럼 네비게이션 상태를 관리하고 감싸주는 최상위 컴포넌트 */}
      <Stack.Navigator initialRouteName="Login">
        {/**여러 화면들을 스택 방식으로 관리하는 컨테이너 */}
        {userToken ? (
          <Stack.Screen name="Main" component={MainScreen} />
        ) : (
          <Stack.Screen
            name="Login"
            component={LoginScreen}
            options={{ headerShown: false }}
          />
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
