import React, { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Admin } from "@/interface/Admin";

interface AdminAuthContextType {
  isAuthenticated: boolean;
  admin: Admin | null;
  login: (token: string, refreshToken: string, adminData: Admin) => void;
  logout: () => Promise<void>;
  isLoading: boolean;
}

const AdminAuthContext = createContext<AdminAuthContextType | undefined>(
  undefined
);

export const AdminAuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [admin, setAdmin] = useState<Admin | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("adminAccessToken");
    const adminStr = localStorage.getItem("admin");

    if (token && adminStr) {
      try {
        const adminData = JSON.parse(adminStr);
        setAdmin(adminData);
        setIsAuthenticated(true);
      } catch (error) {
        console.error("Failed to parse admin data:", error);
        localStorage.removeItem("adminAccessToken");
        localStorage.removeItem("adminRefreshToken");
        localStorage.removeItem("admin");
      }
    }
    setIsLoading(false);
  }, []);

  const login = (token: string, refreshToken: string, adminData: Admin) => {
    localStorage.setItem("adminAccessToken", token);
    localStorage.setItem("adminRefreshToken", refreshToken);
    localStorage.setItem("admin", JSON.stringify(adminData));
    setAdmin(adminData);
    setIsAuthenticated(true);
  };

  const logout = async () => {
    try {
      // 실제 환경에서는 API 호출을 통해 백엔드에 로그아웃 요청을 보냅니다
      // 테스트 환경이므로 API 호출은 생략합니다
      // await adminAuthService.logout();
    } catch (error) {
      console.error("Logout error:", error);
    } finally {
      localStorage.removeItem("adminAccessToken");
      localStorage.removeItem("adminRefreshToken");
      localStorage.removeItem("admin");
      setAdmin(null);
      setIsAuthenticated(false);
      navigate("/admin/login");
    }
  };

  return (
    <AdminAuthContext.Provider
      value={{ isAuthenticated, admin, login, logout, isLoading }}
    >
      {children}
    </AdminAuthContext.Provider>
  );
};

export const useAdminAuth = () => {
  const context = useContext(AdminAuthContext);
  if (context === undefined) {
    throw new Error("useAdminAuth must be used within an AdminAuthProvider");
  }
  return context;
};
