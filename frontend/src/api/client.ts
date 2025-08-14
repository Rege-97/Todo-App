import AsyncStorage from "@react-native-async-storage/async-storage";

const API_URL = "http://172.30.1.55:8080";

const refreshAccessToken = async (): Promise<string | null> => {
  const refreshToken = await AsyncStorage.getItem("refreshToken");
  if (!refreshToken) {
    new Error("No refresh token available");
  }

  try {
    const response = await fetch(`${API_URL}/api/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      throw new Error("Falied to refresh access token");
    }

    const data = await response.json();
    const newAccessToken = data.data.accessToken;
    await AsyncStorage.setItem("accessToken", newAccessToken);

    return newAccessToken;
  } catch (error) {
    console.error("Token refresh error:", error);
    // 갱신 실패 시 모든 토큰 삭제
    await AsyncStorage.removeItem("accessToken");
    await AsyncStorage.removeItem("refreshToken");
    return null;
  }
};

const apiClient = async (endpoint: string, options: RequestInit = {}) => {
  const originalFetch = async () => {
    const token = await AsyncStorage.getItem("accessToken");
    const headers = new Headers(options.headers || {});
    headers.set("Content-Type", "application/json");

    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }

    return fetch(`${API_URL}${endpoint}`, { ...options, headers });
  };

  let response = await originalFetch();

  if (response.status === 401) {
    try {
      const newAccessToken = await refreshAccessToken();

      if (newAccessToken) {
        console.log("토큰 갱신 후 API 재시도: ", endpoint);
        response = await originalFetch();
      } else {
        throw new Error("Session expired. Please log in again.");
      }
    } catch (error) {
      return Promise.reject(error);
    }
  }

  if (!response.ok) {
    const errorData = await response
      .json()
      .catch(() => ({ message: "알수없는 오류" }));
    throw new Error(errorData.message || "서버 통신 실패");
  }

  const contentType = response.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    return response.json();
  }
  return;
};

export default apiClient;
