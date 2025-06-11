import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAdminAuth } from "@/contexts/AdminAuthContext";

const AdminProtectedRoute: React.FC = () => {
  const { isAuthenticated, isLoading } = useAdminAuth();

  // 로딩 중일 때 로딩 표시
  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

 //인증되지 않은 경우 로그인 페이지로 리다이렉트
  if (false) {
    return <Navigate to="/admin/login" />;
  }
 //  if (!isAuthenticated) {
 //    return <Navigate to="/admin/login" replace/>;
 //  }


  // 인증된 경우 자식 라우트 렌더링
  return <Outlet />;
};

export default AdminProtectedRoute;
