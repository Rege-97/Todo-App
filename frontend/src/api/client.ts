import AsyncStorage from "@react-native-async-storage/async-storage";

const API_URL = "http://172.30.1.99:8080";
const apiClient = async (endpoint: string, options: RequestInit = {}) => {
  const token = await AsyncStorage.getItem("accessToken");

  const headers: { [key: string]: string } = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorData = await response
      .json()
      .catch(() => ({ message: "알 수 없는 오류가 발생했습니다." }));
    throw new Error(errorData.message || "서버와의 통신에 실패했습니다.");
  }

  const contentType = response.headers.get("content-type");
  if (contentType && contentType?.indexOf("application/json") !== -1) {
    return response.json();
  } else {
    return;
  }
};

export default apiClient;
