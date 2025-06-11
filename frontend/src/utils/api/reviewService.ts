import axios from "axios";

// 환경 변수에서 API 기본 URL 가져오기
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// API 경로를 위한 환경 변수 - 추가 필요
// env 파일에 추가해야 할 변수들:
// VITE_API_GET_BOOK_REVIEWS=/books/{bookId}/reviews
// VITE_API_CREATE_REVIEW=/books/{bookId}/reviews

// API 응답 인터페이스
export interface ReviewApiResponse {
  status: number;
  message: string;
  data: {
    reviews: Review[];
    pagination: {
      totalElements: number;
      totalPages: number;
      currentPage: number;
      size: number;
    };
    summary: {
      averageRating: number;
      totalReviews: number;
    };
  };
}

// 리뷰 인터페이스
export interface Review {
  id: number;
  email: string;
  rating: number;
  text: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * URL 경로에서 플레이스홀더를 실제 값으로 대체하는 함수
 * @param path API 경로 (예: /books/{bookId}/reviews)
 * @param params 대체할 파라미터 객체 (예: { bookId: "123" })
 * @returns 대체된 경로 (예: /books/123/reviews)
 */
const formatPath = (
  path: string,
  params: Record<string, string | undefined>
): string => {
  let formattedPath = path;

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined) {
      formattedPath = formattedPath.replace(`{${key}}`, value);
    }
  });

  return formattedPath;
};

// 리뷰 서비스 객체
const reviewService = {
  /**
   * 책의 리뷰 목록을 가져오는 함수
   * @param bookId 책 ID
   * @param page 페이지 번호 (기본값: 1)
   * @param size 페이지 크기 (기본값: 6)
   * @returns Promise<ReviewApiResponse>
   */
  getBookReviews: async (
    bookId: string | undefined,
    page: number = 1,
    size: number = 6
  ): Promise<ReviewApiResponse> => {
    try {
      // 환경 변수에서 API 경로 가져오기
      const path = import.meta.env.VITE_API_GET_BOOK_REVIEWS;
      const formattedPath = formatPath(path, { bookId });

      const response = await axios.get<ReviewApiResponse>(
        `${API_BASE_URL}${formattedPath}?page=${page}&size=${size}`
      );
      return response.data;
    } catch (error) {
      console.error("리뷰 데이터 로드 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 책의 최신 리뷰 몇 개만 가져오는 함수
   * @param bookId 책 ID
   * @param count 가져올 리뷰 개수 (기본값: 3)
   * @returns Promise<ReviewApiResponse>
   */
  getLatestReviews: async (
    bookId: string | undefined,
    count: number = 3
  ): Promise<ReviewApiResponse> => {
    try {
      // 환경 변수에서 API 경로 가져오기
      const path = import.meta.env.VITE_API_GET_BOOK_REVIEWS;
      const formattedPath = formatPath(path, { bookId });

      const response = await axios.get<ReviewApiResponse>(
        `${API_BASE_URL}${formattedPath}?page=1&size=${count}`
      );
      return response.data;
    } catch (error) {
      console.error("최신 리뷰 데이터 로드 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 리뷰 작성 함수
   * @param bookId 책 ID
   * @param reviewData 리뷰 데이터 (rating, text)
   * @param accessToken 액세스 토큰
   * @returns Promise<any>
   */
  createReview: async (
    bookId: string | undefined,
    reviewData: { rating: number; text: string },
    accessToken: string
  ): Promise<any> => {
    try {
      // 환경 변수에서 API 경로 가져오기
      const path = import.meta.env.VITE_API_CREATE_REVIEW;
      const formattedPath = formatPath(path, { bookId });

      const response = await axios.post(
        `${API_BASE_URL}${formattedPath}`,
        reviewData,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("리뷰 등록 중 오류 발생:", error);
      throw error;
    }
  },

  // reviewService.ts에 아래 메서드들을 추가합니다

  /**
   * 리뷰 수정 함수
   * @param bookId 책 ID
   * @param reviewId 리뷰 ID
   * @param reviewData 수정할 리뷰 데이터 (rating, text)
   * @param accessToken 액세스 토큰
   * @returns Promise<any>
   */
  updateReview: async (
    bookId: string | undefined,
    reviewId: number,
    reviewData: { rating: number; text: string },
    accessToken: string
  ): Promise<any> => {
    try {
      // API 경로: /books/{bookId}/reviews/{reviewId}
      const response = await axios.put(
        `${API_BASE_URL}/books/${bookId}/reviews/${reviewId}`,
        reviewData,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("리뷰 수정 중 오류 발생:", error);
      throw error;
    }
  },

  /**
   * 리뷰 삭제 함수
   * @param bookId 책 ID
   * @param reviewId 리뷰 ID
   * @param accessToken 액세스 토큰
   * @returns Promise<any>
   */
  deleteReview: async (
    bookId: string | undefined,
    reviewId: number,
    accessToken: string
  ): Promise<any> => {
    try {
      // API 경로: /books/{bookId}/reviews/{reviewId}
      const response = await axios.delete(
        `${API_BASE_URL}/books/${bookId}/reviews/${reviewId}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("리뷰 삭제 중 오류 발생:", error);
      throw error;
    }
  },
};

export default reviewService;
