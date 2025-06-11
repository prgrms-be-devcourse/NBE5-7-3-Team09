// src/types/book.ts

export interface Book {
  id: number;
  title: string;
  author: string;
  cover: string;
  category: string;
  rating?: number;
  publishDate?: string;
  isNew?: boolean;
  isBestseller?: boolean;
  addedDate?: string; // 위시리스트에 추가된 날짜
  description?: string;
  isbn?: string;
  pageCount?: number;
  publisher?: string;
  price?: number;
}

// API 응답에 맞는 타입 정의
export interface Category {
  id: number;
  major: string;
  sub: string;
}

export interface Publisher {
  id: number;
  name: string;
}

export interface Author {
  id: number;
  name: string;
}

export interface BookDetail {
  id: number;
  name: string;
  description: string;
  image: string | null;
  isbn: string;
  ecn: string | null;
  pubDate: string;
  category: Category;
  publisher: Publisher;
  author: Author;
  rating?: number; // 리뷰 평점 (API에 없는 경우 프론트에서 처리)
  reviewCount?: number; // 리뷰 수 (API에 없는 경우 프론트에서 처리)
}

export interface BookDetailResponse {
  status: number;
  message: string;
  data: BookDetail;
}

export interface BookCategory {
  id: string;
  name: string;
  subcategories?: string[];
}

export interface BookSearchParams {
  query?: string;
  category?: string;
  sortBy?:
    | "newest"
    | "oldest"
    | "title_asc"
    | "title_desc"
    | "rating_high"
    | "rating_low";
  page?: number;
  limit?: number;
}

export interface BookSearchResponse {
  books: Book[];
  totalItems: number;
  currentPage: number;
  totalPages: number;
}
