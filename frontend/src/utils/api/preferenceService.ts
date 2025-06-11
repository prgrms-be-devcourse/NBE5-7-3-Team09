// preferenceService.ts 파일 (신규 생성)
import axios from "axios";

const API_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// 인터페이스 정의
export interface BookPreference {
  id: number;
  name: string;
  image: string;
  rating: number;
}

export interface PaginationInfo {
  totalPages: number;
  size: number;
  currentPage: number;
  totalElements: number;
}

export interface PreferenceListResponse {
  code: number;
  message: string;
  data: {
    preferences: BookPreference[];
    pagination: PaginationInfo;
  };
}

// API 호출 함수
export const preferenceService = {
  // 관심도서 목록 조회
  getPreferences: async (
    page: number = 1,
    size: number = 8
  ): Promise<PreferenceListResponse> => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.get(`${API_URL}/preferences`, {
        params: { page, size },
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Failed to fetch preferences:", error);
      throw error;
    }
  },

  // 관심도서 추가 (기존 bookService에서 이동)
  addToPreference: async (bookId: number | string) => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.post(
        `${API_URL}/preferences`,
        { id: bookId },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Error adding to preference:", error);
      throw error;
    }
  },

  // 관심도서 삭제
  removeFromPreference: async (bookId: number | string) => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.delete(`${API_URL}/preferences/${bookId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Error removing from preference:", error);
      throw error;
    }
  },
};

export default preferenceService;
