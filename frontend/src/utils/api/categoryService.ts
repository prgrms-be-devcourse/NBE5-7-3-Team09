// src/utils/api/categoryService.ts
import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// 카테고리 타입 정의
export interface Category {
  id: number;
  major: string;
  subs: string[];
}

// API 응답 타입 정의
export interface CategoryResponse {
  status: number;
  message: string;
  data: {
    categories: Category[];
  };
}

// 카테고리 관련 서비스 API
export const categoryService = {
  // 전체 카테고리 목록 조회
  getCategories: async () => {
    try {
      const response = await axios.get<CategoryResponse>(
        `${API_BASE_URL}/category`
      );
      console.log(response);
      return response.data;
    } catch (error) {
      console.error("Error in getCategories:", error);
      throw error;
    }
  },
};

export default categoryService;
