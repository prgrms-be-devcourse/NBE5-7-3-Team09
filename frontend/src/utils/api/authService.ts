import api from "./axiosConfig";
import LoginForm from "@/interface/LoginForm";
import SignupForm from "@/interface/SignupForm";

export const authService = {
  login: async (credentials: LoginForm) => {
    const response = await api.post(
      import.meta.env.VITE_API_LOGIN,
      credentials
    );

    const accessToken = response.headers["authorization"];
    const refreshToken = response.headers["refresh"];

    if (accessToken) {
      localStorage.setItem("accessToken", accessToken.replace("Bearer ", ""));
    }

    if (refreshToken) {
      localStorage.setItem("refreshToken", refreshToken);
    }

    return {
      accessToken: accessToken?.replace("Bearer ", ""),
      refreshToken,
      email: credentials.email,
    };
  },

  signup: async (signupData: Omit<SignupForm, "confirmPassword">) => {
    const response = await api.post(
      import.meta.env.VITE_API_SIGNUP,
      signupData
    );
    return response.data;
  },

  logout: async () => {
    try {
      const accessToken = localStorage.getItem("accessToken");
      const refreshToken = localStorage.getItem("refreshToken");

      if (!accessToken || !refreshToken) {
        console.warn("로그아웃 시 토큰이 없습니다.");
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("email");
        return { success: true };
      }

      const response = await api.post(
        import.meta.env.VITE_API_LOGOUT,
        { refreshToken: refreshToken }, // 리프레시 토큰을 요청 본문에 포함
        {
          headers: {
            Authorization: `Bearer ${accessToken}`, // 액세스 토큰을 Authorization 헤더에 포함
          },
        }
      );

      console.log(
        "%c 로그아웃 성공 ",
        "background: #4CAF50; color: white; font-size: 12px; font-weight: bold; padding: 4px 8px; border-radius: 4px;"
      );

      // 로그아웃 성공 시 로컬 스토리지 토큰 제거
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("email");

      return response.data;
    } catch (error) {
      console.error("로그아웃 에러:", error);
      // 로그아웃 실패해도 로컬 토큰은 제거
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("email");
      throw error;
    }
  },

  reissueToken: async (refreshToken: string) => {
    console.group("AuthService: Token Refresh Request");
    console.log("Refresh Token 요청 시작");
    console.log(
      "사용 중인 refreshToken:",
      refreshToken.substring(0, 10) + "..."
    );

    try {
      const response = await api.post(
        import.meta.env.VITE_API_REISSUE_TOKEN,
        {},
        {
          headers: {
            Authorization: `Bearer ${refreshToken}`,
          },
        }
      );

      const newAccessToken = response.headers["authorization"];
      const newRefreshToken = response.headers["refresh"];

      console.log(
        "%c AuthService: 토큰 갱신 성공 ",
        "background: #4CAF50; color: white; font-size: 12px; font-weight: bold; padding: 4px 8px; border-radius: 4px;"
      );
      console.log("새 Access Token 수신:", newAccessToken ? "성공" : "실패");
      console.log("새 Refresh Token 수신:", newRefreshToken ? "성공" : "실패");

      if (newAccessToken) {
        const cleanToken = newAccessToken.replace("Bearer ", "");
        localStorage.setItem("accessToken", cleanToken);
        console.log(
          "새 Access Token 저장됨:",
          cleanToken.substring(0, 10) + "..."
        );
      }

      if (newRefreshToken) {
        localStorage.setItem("refreshToken", newRefreshToken);
        console.log(
          "새 Refresh Token 저장됨:",
          newRefreshToken.substring(0, 10) + "..."
        );
      }

      console.groupEnd();
      return response.data;
    } catch (error) {
      console.error(
        "%c AuthService: 토큰 갱신 실패 ",
        "background: #F44336; color: white; font-size: 12px; font-weight: bold; padding: 4px 8px; border-radius: 4px;"
      );
      console.error("에러 정보:", error);
      console.groupEnd();
      throw error;
    }
  },
};
