// src/utils/api/bookService.ts - API 응답 처리 수정
import axios from "axios";
import { BookDetailResponse } from "@/types/book";
import preferenceService from "./preferenceService";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// API 응답 구조에 맞게 타입 정의
interface ApiResponse {
  status: number;
  message: string;
  data: {
    books: any[];
    pagination: {
      totalElements: number;
      totalPages: number;
      currentPage: number;
      size: number;
    };
  };
}

// 기본 Axios 인스턴스 생성
const api = axios.create({
  baseURL: API_BASE_URL,
});

// 완전히 인코딩을 우회하는 함수
const makeRawRequest = async (url: string) => {
  // 최종 API URL 출력
  const fullUrl = `${API_BASE_URL}${url}`;
  console.log("📣 최종 API URL:", fullUrl);

  return new Promise((resolve, reject) => {
    // XHR 객체 생성
    const xhr = new XMLHttpRequest();

    // 요청 설정
    xhr.open("GET", fullUrl, true);

    // 응답 타입 설정
    xhr.responseType = "json";

    // 응답 처리
    xhr.onload = function () {
      // 상태 코드 출력
      console.log("📣 응답 상태 코드:", xhr.status);

      if (xhr.status >= 200 && xhr.status < 300) {
        // 상세한 응답 데이터 출력
        console.log("📣 응답 데이터 전체:", xhr.response);

        // 응답 데이터 구조 확인 및 가공
        const response = xhr.response;
        let processedResponse = {
          status: 200,
          message: "성공",
          data: {
            books: [],
            pagination: {
              totalElements: 0,
              totalPages: 1,
              currentPage: 1,
              size: 20,
            },
          },
        };

        // 응답 구조가 다를 경우 처리
        if (response) {
          // API 응답의 코드 필드 확인
          if (response.code) {
            processedResponse.status = response.code;
          } else if (response.status) {
            processedResponse.status = response.status;
          }

          // API 응답의 메시지 필드 확인
          if (response.message) {
            processedResponse.message = response.message;
          }

          // API 응답의 data 필드 확인
          if (response.data) {
            // books 데이터 확인 및 설정
            if (response.data.books) {
              processedResponse.data.books = response.data.books;
              console.log("📣 books 데이터 길이:", response.data.books.length);
            }

            // pagination 정보 확인 및 설정
            if (response.data.pagination) {
              processedResponse.data.pagination = {
                totalElements: response.data.pagination.totalElements || 0,
                totalPages: response.data.pagination.totalPages || 1,
                // 페이지 번호를 1부터 시작하도록 처리
                currentPage: response.data.pagination.currentPage || 1,
                size: response.data.pagination.size || 20,
              };
              console.log(
                "📣 pagination 정보:",
                processedResponse.data.pagination
              );
            }
          } else {
            console.log("📣 응답에 data 필드가 없음!");
          }
        }

        resolve(processedResponse);
      } else {
        console.error("📣 HTTP 에러:", xhr.status, xhr.statusText);
        console.error("📣 응답 데이터:", xhr.response);
        reject(new Error(`HTTP 에러: ${xhr.status}`));
      }
    };

    // 에러 처리
    xhr.onerror = function () {
      console.error("📣 네트워크 요청 실패");
      reject(new Error("네트워크 요청 실패"));
    };

    // 요청 헤더 설정
    xhr.setRequestHeader("Accept", "application/json");

    // 요청 전송
    xhr.send();
    console.log("📣 요청 전송 완료");
  });
};

// 책 관련 서비스 API
export const bookService = {
  // 책 목록 조회 (검색, 카테고리 필터링 포함)
  getBooks: async (url: string) => {
    try {
      console.log("📣 요청 URL(원본):", url);

      // XMLHttpRequest를 사용하여 인코딩을 우회
      const response = await makeRawRequest(url);
      return response;
    } catch (error) {
      console.error("📣 Error in getBooks:", error);
      throw error;
    }
  },

  // 책 상세 정보 조회
  getBookDetail: async (bookId: number | string) => {
    try {
      const response = await api.get<BookDetailResponse>(`/books/${bookId}`);
      console.log(`${API_BASE_URL}/books/${bookId}`);
      return response;
    } catch (error) {
      console.error("Error in getBookDetail:", error);
      throw error;
    }
  },

  // 책 리뷰 목록 조회
  getBookReviews: async (bookId: number | string, page = 1, size = 10) => {
    try {
      const response = await api.get(
        `/books/${bookId}/reviews?page=${page}&size=${size}`
      );
      return response.data;
    } catch (error) {
      console.error("Error in getBookReviews:", error);
      throw error;
    }
  },

  // 책 리뷰 작성
  createBookReview: async (bookId: number | string, data: any) => {
    try {
      const response = await api.post(`/books/${bookId}/reviews`, data);
      return response.data;
    } catch (error) {
      console.error("Error in createBookReview:", error);
      throw error;
    }
  },

  // 책 찜하기/찜 취소
  toggleWishlist: async (bookId: number | string) => {
    try {
      // preferenceService를 사용하여 관심도서 추가
      const response = await preferenceService.addToPreference(bookId);
      return response;
    } catch (error) {
      console.error("Error in toggleWishlist:", error);
      throw error;
    }
  },

  // 내 서재에 책 추가
  addToLibrary: async (bookId: number | string) => {
    try {
      // 기존 코드 유지...
      const librariesResponse = await api.get(`/library`, {
        params: { page: 0, size: 1 },
        headers: {
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
      });

      let libraryId;

      if (
        librariesResponse.data.data.allLibraries &&
        librariesResponse.data.data.allLibraries.length > 0
      ) {
        libraryId = librariesResponse.data.data.allLibraries[0].id;
      } else {
        const createResponse = await api.post(
          `/library`,
          { libraryName: "내 라이브러리1" },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
              "Content-Type": "application/json",
            },
          }
        );
        libraryId = createResponse.data.data.id;
      }

      const response = await api.post(
        `/library/${libraryId}`,
        { bookId },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            "Content-Type": "application/json",
          },
        }
      );

      return response.data;
    } catch (error) {
      console.error("Error in addToLibrary:", error);
      throw error;
    }
  },
};

export default bookService;
