import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import LoginScreen from "./src/screens/LoginScreen";

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer> {/**브라우저 라우터 처럼 네비게이션 상태를 관리하고 감싸주는 최상위 컴포넌트 */}
      <Stack.Navigator>  {/**여러 화면들을 스택 방식으로 관리하는 컨테이너 */}
        <Stack.Screen name="Login" component={LoginScreen} /> {/**개별 화면을 등록하는 컴포넌트 */}
      </Stack.Navigator>
    </NavigationContainer>
  );
}

