import api from "./axiosConfig";
import { jwtDecode } from "jwt-decode";
import { Admin } from "@/interface/Admin";

interface AdminLoginForm {
  email: string;
  password: string;
}

export const adminAuthService = {
  /**
   * 관리자 로그인 (공통 /user/login 사용)
   */
  login: async (credentials: AdminLoginForm) => {
    const response = await api.post(
        import.meta.env.VITE_API_LOGIN, // 예: "/user/login"
        credentials
    );

    const accessToken = response.headers["authorization"];
    const refreshToken = response.headers["refresh"];

    if (!accessToken || !refreshToken) {
      throw new Error("토큰이 없습니다. 관리자 로그인 실패");
    }

    const cleanAccessToken = accessToken.replace("Bearer ", "");
    localStorage.setItem("accessToken", cleanAccessToken);
    localStorage.setItem("refreshToken", refreshToken);

    // 관리자 권한 판단용 디코딩
    const decoded: any = jwtDecode(cleanAccessToken);
    const admin: Admin = {
      id: decoded.sub,
      email: decoded.email || credentials.email,
      name: decoded.name || "관리자",
      role: decoded.authorities?.includes("ROLE_ADMIN") ? "ADMIN" : "USER",
    };

    return {
      accessToken: cleanAccessToken,
      refreshToken,
      admin,
    };
  },

  /**
   * 로그아웃 처리 (/user/logout 사용)
   */
  logout: async () => {
    try {
      const accessToken = localStorage.getItem("accessToken");
      const refreshToken = localStorage.getItem("refreshToken");

      if (!accessToken || !refreshToken) {
        console.warn("로그아웃 시 토큰이 없습니다.");
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        return { success: true };
      }

      const response = await api.post(
          import.meta.env.VITE_API_LOGOUT, // 예: "/user/logout"
          { refreshToken },
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
            },
          }
      );

      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");

      return response.data;
    } catch (error) {
      console.error("로그아웃 에러:", error);
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      throw error;
    }
  },

  /**
   * 현재 관리자 토큰 디코딩 (role 확인용)
   */
  getDecodedAdminToken: () => {
    const token = localStorage.getItem("accessToken");
    if (!token) return null;

    try {
      return jwtDecode(token);
    } catch (err) {
      console.error("JWT 디코딩 실패:", err);
      return null;
    }
  },
};
