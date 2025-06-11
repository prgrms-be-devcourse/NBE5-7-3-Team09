import { useState, useEffect } from "react";
import { Heart, ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import {
  preferenceService,
  BookPreference,
} from "@/utils/api/preferenceService";
import { useNavigate } from "react-router-dom";
import BookCover from "@/components/book/BookCover";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
  DialogDescription,
} from "@/components/ui/dialog";

interface Book {
  id: number;
  title: string;
  author: string;
  category: string;
  rating?: number;
  addedDate: string;
  image?: string | null;
}

const PreferencePage = () => {
  const navigate = useNavigate();

  // 상태 관리
  const [books, setBooks] = useState<Book[]>([]);
  const [filteredBooks, setFilteredBooks] = useState<Book[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState("newest");

  // API 관련 상태
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [pageSize, setPageSize] = useState(8);

  // 책 삭제 관련 상태
  const [bookToDelete, setBookToDelete] = useState<Book | null>(null);
  const [openDeleteDialog, setOpenDeleteDialog] = useState<boolean>(false);

  // 기본 이미지 경로
  const defaultCoverImage = "/placeholder-book.png";

  // 초기 데이터 로드
  useEffect(() => {
    fetchPreferences();
  }, [currentPage, pageSize]);

  // 관심도서 데이터 가져오기
  const fetchPreferences = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await preferenceService.getPreferences(
        currentPage,
        pageSize
      );

      // API 응답 처리
      if (response && response.data) {
        const { preferences, pagination } = response.data;

        // 페이지네이션 정보 업데이트
        if (pagination) {
          setTotalPages(pagination.totalPages);
          setCurrentPage(pagination.currentPage);
        }

        // 책 목록 데이터 변환
        const bookList: Book[] = preferences.map((item: BookPreference) => ({
          id: item.id,
          title: item.name,
          author: "저자 정보", // API 응답에 저자 정보가 없어서 임시로 설정
          category: "분류", // API 응답에 카테고리 정보가 없어서 임시로 설정
          rating: item.rating, // API 응답에 평점 정보가 없어서 임시로 설정
          addedDate: new Date().toISOString().split("T")[0], // 추가 날짜 정보 없음
          image: item.image,
        }));

        setBooks(bookList);
        setFilteredBooks(bookList);
      }
    } catch (err) {
      console.error("관심도서 로드 중 오류 발생:", err);
      setError("관심도서를 불러오는데 실패했습니다.");
      toast.error("관심도서 로드 실패", {
        description: "관심도서를 불러오는데 실패했습니다. 다시 시도해주세요.",
      });
    } finally {
      setIsLoading(false);
    }
  };

  // 필터링 및 정렬 (카테고리 필터링 제외)
  useEffect(() => {
    let result = [...books];

    // 검색어 필터링
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(
        (book) =>
          book.title.toLowerCase().includes(query) ||
          book.author.toLowerCase().includes(query)
      );
    }

    // 정렬
    switch (sortBy) {
      case "newest":
        result.sort(
          (a, b) =>
            new Date(b.addedDate).getTime() - new Date(a.addedDate).getTime()
        );
        break;
      case "oldest":
        result.sort(
          (a, b) =>
            new Date(a.addedDate).getTime() - new Date(b.addedDate).getTime()
        );
        break;
      case "title_asc":
        result.sort((a, b) => a.title.localeCompare(b.title));
        break;
      case "title_desc":
        result.sort((a, b) => b.title.localeCompare(a.title));
        break;
      case "rating_high":
        result.sort((a, b) => (b.rating || 0) - (a.rating || 0));
        break;
      case "rating_low":
        result.sort((a, b) => (a.rating || 0) - (b.rating || 0));
        break;
    }

    setFilteredBooks(result);
  }, [books, searchQuery, sortBy]);

  // BookCover 컴포넌트에 필요한 형태로 책 데이터 변환
  const mapBookToBookCoverProps = (book: Book) => {
    return {
      id: book.id,
      title: book.title,
      author: book.author,
      // 이미지가 null이거나 빈 문자열이면 기본 이미지 사용
      cover:
        book.image && book.image.trim() !== "" ? book.image : defaultCoverImage,
      category: book.category,
      rating: book.rating,
      publishDate: book.addedDate,
      isNew: false,
      isBestseller: false,
    };
  };

  // 책 삭제 다이얼로그 열기
  const handleDeleteClick = (bookId: number, e: React.MouseEvent) => {
    // 삭제할 책 찾기 - ID 필드 확인
    const bookToDelete = books.find((book) => book.id === bookId);
    if (bookToDelete) {
      setBookToDelete(bookToDelete);
      setOpenDeleteDialog(true);
    }
  };

  // 책 삭제
  const handleDeleteBook = async () => {
    if (!bookToDelete) return;

    try {
      await preferenceService.removeFromPreference(bookToDelete.id);

      // 성공적으로 삭제되면 UI에서도 제거
      setBooks(books.filter((book) => book.id !== bookToDelete.id));

      setOpenDeleteDialog(false);
      setBookToDelete(null);

      toast.success("도서 삭제", {
        description: "관심 도서에서 삭제되었습니다.",
      });
    } catch (err) {
      console.error("책 삭제 중 오류 발생:", err);
      toast.error("삭제 실패", {
        description: "관심 도서 삭제에 실패했습니다. 다시 시도해주세요.",
      });
    }
  };

  // 책 상세보기
  const handleBookClick = (id: number) => {
    navigate(`/book/${id}`);
  };

  // 책 우클릭 메뉴 (컨텍스트 메뉴)
  const handleContextMenu = (e: React.MouseEvent, book: Book) => {
    e.preventDefault();
    setBookToDelete(book);
    setOpenDeleteDialog(true);
  };

  // 페이지 변경 핸들러
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return (
    <div className="container mx-auto">
      {/* 헤더 */}
      <div className="mb-4">
        <Button
          variant="ghost"
          onClick={() => navigate(-1)}
          className="mb-4 pl-0"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          돌아가기
        </Button>

        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">관심 도서</h1>
          <Badge variant="outline" className="px-2 py-1">
            총 {filteredBooks.length}권
          </Badge>
        </div>
      </div>

      {/* 로딩 상태 */}
      {isLoading && (
        <div className="flex justify-center items-center py-20">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <span className="ml-2">불러오는 중...</span>
        </div>
      )}

      {/* 에러 메시지 */}
      {error && (
        <div className="bg-red-50 p-4 rounded-md mb-4">
          <p className="text-red-600">{error}</p>
          <Button
            variant="outline"
            className="mt-2"
            onClick={() => fetchPreferences()}
          >
            다시 시도
          </Button>
        </div>
      )}

      {/* 도서 목록 */}
      {!isLoading &&
        !error &&
        (filteredBooks.length === 0 ? (
          <div className="bg-gray-50 rounded-lg p-8 text-center">
            <Heart className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <h3 className="text-lg font-medium mb-2">
              {searchQuery ? "검색 결과가 없습니다" : "관심 도서가 없습니다"}
            </h3>
            <p className="text-gray-600 mb-4">
              {searchQuery
                ? "다른 검색어를 사용해보세요."
                : "관심있는 도서를 추가해보세요."}
            </p>
            <Button
              variant="outline"
              onClick={() => {
                setSearchQuery("");
              }}
            >
              {searchQuery ? "검색 초기화" : "도서 탐색하기"}
            </Button>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-5">
              {filteredBooks.map((book) => (
                <div
                  key={book.id}
                  className="relative"
                  onContextMenu={(e) => handleContextMenu(e, book)}
                >
                  <BookCover
                    book={mapBookToBookCoverProps(book)}
                    onClick={handleBookClick}
                    className="w-full"
                    // 삭제 버튼 관련 props 추가
                    showDeleteButton={true}
                    onDelete={handleDeleteClick}
                  />
                </div>
              ))}
            </div>

            {/* 페이지네이션 */}
            {totalPages > 1 && (
              <div className="flex justify-center mt-8">
                <div className="flex space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={currentPage === 1}
                    onClick={() => handlePageChange(currentPage - 1)}
                  >
                    이전
                  </Button>
                  <span className="py-2 px-4 bg-gray-100 rounded-md">
                    {currentPage} / {totalPages}
                  </span>
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={currentPage === totalPages}
                    onClick={() => handlePageChange(currentPage + 1)}
                  >
                    다음
                  </Button>
                </div>
              </div>
            )}
          </>
        ))}

      {/* 책 삭제 확인 다이얼로그 */}
      <Dialog open={openDeleteDialog} onOpenChange={setOpenDeleteDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>관심 도서 삭제</DialogTitle>
            <DialogDescription>
              "{bookToDelete?.title}"을(를) 관심 도서에서 삭제하시겠습니까? 이
              작업은 되돌릴 수 없습니다.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="mt-4">
            <DialogClose asChild>
              <Button variant="outline">취소</Button>
            </DialogClose>
            <Button onClick={handleDeleteBook} variant="destructive">
              삭제하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default PreferencePage;
