// src/pages/book/CategoryRedirect.tsx (기존 카테고리 URL 지원을 위한 리다이렉트 컴포넌트)
import { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";

// 기존 /category/:id URL 주소를 새로운 /books?category=id 형식으로 리다이렉트하는 컴포넌트
const CategoryRedirect: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    // 카테고리 ID가 있으면 /books?category=id로 리다이렉트
    if (id) {
      navigate(`/books?category=${id}`, { replace: true });
    } else {
      // ID가 없으면 전체 도서 목록으로 리다이렉트
      navigate("/books", { replace: true });
    }
  }, [id, navigate]);

  // 리다이렉트 중임을 표시하는 로딩 화면 또는 빈 컴포넌트 반환
  return <div className="container mx-auto py-20">리다이렉트 중...</div>;
};

export default CategoryRedirect;
