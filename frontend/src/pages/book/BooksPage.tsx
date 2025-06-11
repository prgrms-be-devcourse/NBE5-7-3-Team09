// src/pages/book/BooksPage.tsx - API 연동 수정
import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Book } from "lucide-react";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { Pagination } from "@/components/ui/pagination";
import { Skeleton } from "@/components/ui/skeleton";
import { bookService } from "@/utils/api/bookService";
import { categoryService, Category } from "@/utils/api/categoryService";
import BookCover from "@/components/book/BookCover";

// API 응답에 맞는 책 타입 정의
interface BookItem {
  id: number;
  name: string;
  image: string | null;
  categoryMajor: string;
  categorySub: string;
  authorName: string;
  rating: number;
}

const BooksPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // URL에서 파라미터 직접 추출
  const getQueryParams = () => {
    const params = new URLSearchParams(location.search);
    const keywordParam = params.get("keyword");
    const categoryParam = params.get("category_major");
    const pageParam = params.get("page");

    console.log("📣 URL 파라미터 확인:", {
      keyword: keywordParam,
      category_major: categoryParam,
      page: pageParam,
    });

    return {
      keyword: keywordParam || "",
      categoryId: categoryParam || "0",
      page: parseInt(pageParam || "1"),
    };
  };

  const { keyword, categoryId, page } = getQueryParams();

  const [books, setBooks] = useState<BookItem[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoadingCategories, setIsLoadingCategories] = useState(true);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [currentPage, setCurrentPage] = useState<number>(page);
  const pageSize = 20;

  // 카테고리 목록 불러오기
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setIsLoadingCategories(true);
        const response = await categoryService.getCategories();
        if (response.status === 200) {
          // API 응답 구조에 맞게 수정: categories 필드 사용
          setCategories(response.data.categories || []);
        } else {
          throw new Error("카테고리 목록을 불러오는데 실패했습니다.");
        }
      } catch (error) {
        console.error("Error fetching categories:", error);
        toast.error("카테고리 불러오기 실패", {
          description: "카테고리 목록을 불러오는 중 오류가 발생했습니다.",
        });
        setCategories(defaultCategories);
      } finally {
        setIsLoadingCategories(false);
      }
    };

    fetchCategories();
  }, []);

  // 현재 선택된 카테고리명 가져오기
  const getCurrentCategoryName = () => {
    if (categoryId === "0") {
      return "전체";
    }
    const category = categories.find((cat) => cat.id === parseInt(categoryId));
    return category ? category.major : "전체";
  };

  // 페이지 제목 설정
  const getPageTitle = () => {
    if (keyword) {
      return `"${keyword}" 검색 결과`;
    } else {
      return `${getCurrentCategoryName()} 도서`;
    }
  };

  // 인코딩 없이 페이지 이동
  const navigateWithoutEncoding = (
    path: string,
    params: Record<string, string>
  ) => {
    let url = path;
    const queryParams = [];

    for (const key in params) {
      if (params[key]) {
        queryParams.push(`${key}=${params[key]}`);
      }
    }

    if (queryParams.length > 0) {
      url += `?${queryParams.join("&")}`;
    }

    console.log("📣 페이지 이동 URL:", url);

    // 현재 URL을 직접 변경
    window.location.href = url;
  };

  // 책 목록 조회 - 검색, 카테고리, 페이지 변경 시 호출
  useEffect(() => {
    console.log("📣 useEffect 실행 - 파라미터:", {
      keyword,
      categoryId,
      page,
      pageSize,
    });

    let isMounted = true;

    const fetchData = async () => {
      setIsLoading(true);
      try {
        // API 요청 URL 구성 수정
        // 페이지는 1부터 시작하게 변경
        let url = `/books?page=${page}&size=${pageSize}`;

        // 검색어가 있으면 추가
        if (keyword) {
          url = `/books/search?keyword=${keyword}&page=${page}&size=${pageSize}`;
        } else {
          // 카테고리 필터 추가 - 0이 아닐 때만
          if (categoryId && categoryId !== "0") {
            // URL 구성 수정: 검색이 없을 때는 /books 엔드포인트 사용, 카테고리는 category_major 파라미터
            url = `/books?category_major=${categoryId}&page=${page}&size=${pageSize}`;
          }
        }

        console.log("📣 요청 전송할 URL:", url);
        const response = await bookService.getBooks(url);
        console.log("📣 응답 받음:", response);

        // 응답 처리
        if (isMounted) {
          if (response && response.status === 200) {
            if (response.data && response.data.books) {
              console.log("📣 책 데이터 세팅:", response.data.books.length);
              setBooks(response.data.books);
              setTotalPages(response.data.pagination.totalPages || 1);
              setTotalElements(response.data.pagination.totalElements || 0);
              setCurrentPage(response.data.pagination.currentPage || 1); // 1부터 시작하는 페이지 번호
            } else {
              console.error("📣 데이터 구조 오류:", response.data);
              throw new Error("응답 데이터 구조가 예상과 다릅니다.");
            }
          } else {
            console.error("📣 API 오류 응답:", response);
            throw new Error(
              `API 오류: ${response?.message || "알 수 없는 오류"}`
            );
          }
        }
      } catch (error) {
        if (isMounted) {
          console.error("📣 Error fetching books:", error);
          toast.error("책 목록 조회 실패", {
            description: "책 목록을 불러오는 중 오류가 발생했습니다.",
          });
          setBooks([]);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    };

    fetchData();

    return () => {
      isMounted = false;
    };
  }, [categoryId, keyword, page, pageSize]);

  // 페이지 변경 처리
  const handlePageChange = (newPage: number) => {
    console.log("📣 페이지 변경:", newPage);

    let newParams = {
      page: newPage.toString(),
    };

    if (keyword) {
      newParams["keyword"] = keyword;
    }

    if (categoryId && categoryId !== "0") {
      newParams["category_major"] = categoryId;
    }

    // location.href를 사용하여 인코딩 우회
    navigateWithoutEncoding("/books", newParams);
  };

  // 책 상세 페이지로 이동
  const handleBookClick = (bookId: number) => {
    navigate(`/book/${bookId}`);
  };

  // 기본 카테고리 목록 (API 에러 시 사용)
  const defaultCategories: Category[] = [
    {
      id: 0,
      major: "전체",
      subs: ["백과사전", "도서관학", "저널리즘", "전집", "연속간행물"],
    },
    {
      id: 100,
      major: "철학",
      subs: ["형이상학", "인식론", "논리학", "윤리학", "심리학"],
    },
    {
      id: 200,
      major: "종교",
      subs: ["비교종교", "불교", "기독교", "천주교", "이슬람교"],
    },
    // ... 나머지 카테고리 ...
  ];

  // 로딩 중 스켈레톤 UI
  const renderSkeletons = () => {
    return Array(12)
      .fill(0)
      .map((_, index) => (
        <div key={`skeleton-${index}`}>
          <Skeleton className="w-full h-60 md:h-96" />
          <div className="mt-2">
            <Skeleton className="h-4 w-full mb-1" />
            <Skeleton className="h-3 w-2/3" />
          </div>
        </div>
      ));
  };

  // 전체 도서 보기 버튼 클릭 처리
  const handleViewAllBooks = () => {
    window.location.href = "/books";
  };

  // 컴포넌트가 마운트될 때 로그 추가
  useEffect(() => {
    console.log("📣 BooksPage 컴포넌트 마운트");
    console.log("📣 현재 URL:", window.location.href);
    return () => {
      console.log("📣 BooksPage 컴포넌트 언마운트");
    };
  }, []);

  console.log("📣 렌더링 - 상태:", {
    isLoading,
    booksCount: books.length,
    totalPages,
    totalElements,
    currentPage,
  });

  return (
    <div className="container mx-auto">
      {/* 헤더 섹션 */}
      <div className="mb-8">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
          <h1 className="text-2xl font-bold mb-4 sm:mb-0">{getPageTitle()}</h1>
        </div>

        {/* 검색 결과 정보 */}
        {!isLoading && books.length > 0 && (
          <p className="text-gray-600">
            총 {totalElements}개의 도서를 찾았습니다.
          </p>
        )}
      </div>

      {/* 책 그리드 */}
      {isLoading ? (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
          {renderSkeletons()}
        </div>
      ) : books.length === 0 ? (
        <div className="bg-gray-50 rounded-lg p-8 text-center">
          <Book className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium mb-2">책이 없습니다</h3>
          <p className="text-gray-600 mb-4">
            {keyword
              ? `"${keyword}"에 대한 검색 결과가 없습니다.`
              : categoryId !== "0"
              ? `"${getCurrentCategoryName()}" 카테고리에 등록된 책이 없습니다.`
              : "등록된 책이 없습니다."}
          </p>
          <Button
            variant="default"
            onClick={handleViewAllBooks}
            className="bg-blue-500 hover:bg-blue-600"
          >
            전체 도서 보기
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
          {books.map((book) => (
            <BookCover
              key={book.id}
              book={{
                id: book.id,
                title: book.name,
                author: book.authorName,
                cover: book.image || "",
                category: book.categoryMajor,
                rating: book.rating,
              }}
              onClick={handleBookClick}
            />
          ))}
        </div>
      )}

      {/* 페이지네이션 */}
      {!isLoading && books.length > 0 && totalPages > 1 && (
        <div className="mt-8">
          <Pagination
            pageCount={totalPages}
            currentPage={currentPage} // 1부터 시작하는 페이지 번호 사용
            onPageChange={handlePageChange}
          />
        </div>
      )}
    </div>
  );
};

export default BooksPage;
