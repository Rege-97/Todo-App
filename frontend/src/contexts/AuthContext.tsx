import AsyncStorage from "@react-native-async-storage/async-storage";
import React, { createContext, useContext, useEffect, useState } from "react";

type AuthContextType = {
  userToken: string | null;
  isLoading: boolean;
  signIn: (acess: string, refresh: string) => Promise<void>;
  signOut: () => Promise<void>;
};

// Context 생성
const AuthContext = createContext<AuthContextType | undefined>(undefined); // AuthContextType 이거나 undefined, 현재는 undefined

// 다른 컴포넌트들을 감싸서 Context를 제공할 Provider 컴포넌트
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [userToken, setUserToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // 앱 시작 시 토큰 확인
    const checkToken = async () => {
      try {
        const token = await AsyncStorage.getItem("accessToken");
        setUserToken(token);
      } catch (error) {
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    };
    checkToken();
  }, []);

  // 로그인 처리 함수
  const signIn = async (access: string, refresh: string) => {
    setIsLoading(true);
 try {
      await AsyncStorage.setItem('accessToken', access);
      await AsyncStorage.setItem('refreshToken', refresh);
      setUserToken(access);
    } catch (e) {
      console.error('토큰 저장 실패', e);
    } finally {
      setIsLoading(false);
    }
  };

  // 로그아웃 처리 함수
  const signOut = async () => {
    setIsLoading(true);
 try {
      await AsyncStorage.removeItem('accessToken');
      await AsyncStorage.removeItem('refreshToken');
      setUserToken(null);
    } catch (e) {
      console.error('토큰 삭제 실패', e);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthContext.Provider value={{ userToken, isLoading, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
};

// 다른 컴포넌트에서 Context를 쉽게 사용하기 위한 Custom Hook
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth는 AuthProvider 안에서만 사용해야 합니다.");
  }
  return context;
};
