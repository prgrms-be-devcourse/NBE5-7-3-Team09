import axios from "axios";

const API_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// 인터페이스 정의
export interface Library {
  library_id: number;
  library_name: string;
  created_at: string;
  updated_at: string;
}

export interface Book {
  book_id: number;
  book_name: string;
  book_image: string;
}

export interface LibraryResponse {
  code: number;
  message: string;
  data: {
    libraries: Library[];
    totalPages?: number;
    currentPage?: number;
  };
}

export interface LibraryDetailResponse {
  code: number;
  message: string;
  data: {
    books: Book[];
    totalPages?: number;
    currentPage?: number;
  };
}

// API 응답 데이터 변환 함수
const transformLibraryData = (apiResponse: any): LibraryResponse => {
  // 라이브러리가 없는 경우
  if (
    !apiResponse.data.allLibraries ||
    apiResponse.data.allLibraries.length === 0
  ) {
    return {
      code: apiResponse.status,
      message: apiResponse.message,
      data: {
        libraries: [],
        totalPages: 0,
        currentPage: 1,
      },
    };
  }

  return {
    code: apiResponse.status,
    message: apiResponse.message,
    data: {
      libraries: apiResponse.data.allLibraries.map((lib: any) => ({
        library_id: lib.id,
        library_name: lib.libraryName,
        created_at: lib.createAt,
        updated_at: lib.updateAt,
      })),
      totalPages: Math.ceil(
        apiResponse.data.totalCount / apiResponse.data.size
      ),
      currentPage: apiResponse.data.page,
    },
  };
};

// API 호출 함수
export const libraryService = {
  // 라이브러리 목록 조회
  getLibraries: async (
    page: number = 0,
    size: number = 10
  ): Promise<LibraryResponse> => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.get(`${API_URL}/library`, {
        params: { page, size },
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      return transformLibraryData(response.data);
    } catch (error) {
      console.error("Failed to fetch libraries:", error);
      throw error;
    }
  },

  // 라이브러리의 책 목록 조회 (새 API 엔드포인트에 맞게 업데이트)
  getLibraryBooks: async (
    libraryId: number,
    page: number = 0,
    size: number = 12
  ) => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.get(
        `${API_URL}/library/${libraryId}/library-books`,
        {
          params: { page, size },
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      console.log(response);
      return response.data;
    } catch (error) {
      console.error(`Failed to fetch books for library ${libraryId}:`, error);
      throw error;
    }
  },

  // 라이브러리 생성
  createLibrary: async (libraryName: string) => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.post(
        `${API_URL}/library`,
        { libraryName }, // API 요청 형식에 맞게 변경
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Failed to create library:", error);
      throw error;
    }
  },

  // 라이브러리 이름 수정
  updateLibraryName: async (libraryId: number, name: string) => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.put(
        `${API_URL}/library/${libraryId}`,
        { libraryName: name }, // API 요청 형식에 맞게 변경
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error(`Failed to update library ${libraryId}:`, error);
      throw error;
    }
  },

  // 라이브러리 삭제
  deleteLibrary: async (libraryId: number) => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.delete(`${API_URL}/library/${libraryId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });

      return response.data;
    } catch (error) {
      console.error(`Failed to delete library ${libraryId}:`, error);
      throw error;
    }
  },

  // 라이브러리에 책 추가
  addBookToLibrary: async (libraryId: number, bookId: number | string) => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.post(
        `${API_URL}/library/${libraryId}`,
        { bookId },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error(
        `Failed to add book ${bookId} to library ${libraryId}:`,
        error
      );
      throw error;
    }
  },

  removeBookFromLibrary: async (libraryId: number, bookId: number) => {
    const accessToken = localStorage.getItem("accessToken");
    try {
      const response = await axios.delete(
        `${API_URL}/library/${libraryId}/library-books/${bookId}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error(
        `Failed to remove book ${bookId} from library ${libraryId}:`,
        error
      );
      throw error;
    }
  },
};
