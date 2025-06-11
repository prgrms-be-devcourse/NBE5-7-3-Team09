import React, { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useNavigate } from "react-router-dom";
import { useAdminAuth } from "@/contexts/AdminAuthContext";
import { adminAuthService } from "@/utils/api/adminAuthService";
import { LockIcon, MailIcon } from "lucide-react";

interface AdminLoginForm {
  email: string;
  password: string;
}

export default function AdminLoginPage() {
  const navigate = useNavigate();
  const { login } = useAdminAuth();
  const [formData, setFormData] = useState<AdminLoginForm>({
    email: "",
    password: "",
  });
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  // ✅ 로그인된 상태이고 ROLE_ADMIN이면 자동 리디렉트
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) return; // ✅ 토큰 없으면 아무 것도 하지 않음 (stay on login page)

    try {
      const decoded: any = jwtDecode(token);
      const isAdmin = decoded?.authorities?.includes("ROLE_ADMIN");
      if (isAdmin) {
        navigate("/admin"); // ✅ 관리자일 경우에만 대시보드로 이동
      }
      // ✅ 일반 유저면 stay (리디렉트 안 함)
    } catch (err) {
      console.warn("잘못된 토큰 형식 또는 디코딩 실패:", err);
    }
  }, []);


  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [id]: value,
    }));

    if (error) {
      setError("");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const { accessToken, refreshToken, admin } = await adminAuthService.login(
          formData
      );
      login(accessToken, refreshToken, admin);
      navigate("/admin");
    } catch (error) {
      setError(
          error instanceof Error ? error.message : "로그인에 실패했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
      <div className="flex justify-center items-center min-h-screen bg-gray-100">
        <Card className="w-full max-w-md">
          <CardHeader className="space-y-1">
            <CardTitle className="text-2xl text-center">관리자 로그인</CardTitle>
          </CardHeader>
          <form onSubmit={handleSubmit} className="space-y-4">
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">이메일</Label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                    <MailIcon className="h-5 w-5" />
                  </div>
                  <Input
                      id="email"
                      type="email"
                      placeholder="admin@example.com"
                      value={formData.email}
                      onChange={handleInputChange}
                      className="pl-10"
                      required
                  />
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="password">비밀번호</Label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                    <LockIcon className="h-5 w-5" />
                  </div>
                  <Input
                      id="password"
                      type="password"
                      value={formData.password}
                      onChange={handleInputChange}
                      className="pl-10"
                      required
                  />
                </div>
              </div>
              {error && (
                  <div className="p-3 text-sm text-red-500 bg-red-50 border border-red-200 rounded-md">
                    {error}
                  </div>
              )}
            </CardContent>
            <CardFooter>
              <Button
                  type="submit"
                  className="w-full bg-blue-600 hover:bg-blue-700"
                  disabled={isLoading}
              >
                {isLoading ? "로그인 중..." : "관리자 로그인"}
              </Button>
            </CardFooter>
          </form>
        </Card>
      </div>
  );
}
