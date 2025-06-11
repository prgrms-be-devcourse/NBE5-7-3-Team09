// src/pages/library/LibraryDetailPage.tsx
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Edit, Book } from "lucide-react";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
  DialogDescription,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { libraryService } from "@/utils/api/libraryService";
import BookCover from "@/components/book/BookCover";

// 타입 정의
interface LibraryBook {
  bookId: number;
  bookName: string;
  bookImage: string | null;
  bookIsbn: string;
  bookEcn: string | null;
  bookPubDate: string;
  bookUpdateAt: string;
  rating: number;
}

interface LibraryDetail {
  libraryId: number;
  libraryName: string;
  createdAt: string;
  updatedAt: string;
}

interface LibraryBooksResponse {
  status: number;
  message: string;
  data: {
    libraryDto: LibraryDetail;
    allLibraryBooks: LibraryBook[];
    totalCount: number;
    page: number;
    size: number;
  };
}

const LibraryDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const libraryId = parseInt(id || "0");
  const navigate = useNavigate();

  const [books, setBooks] = useState<LibraryBook[]>([]);
  const [libraryInfo, setLibraryInfo] = useState<LibraryDetail | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [editLibraryName, setEditLibraryName] = useState<string>("");
  const [openEditDialog, setOpenEditDialog] = useState<boolean>(false);
  const [pageSize] = useState<number>(12); // 페이지당 표시할 책 수

  // 책 삭제 관련 상태
  const [bookToDelete, setBookToDelete] = useState<LibraryBook | null>(null);
  const [openDeleteDialog, setOpenDeleteDialog] = useState<boolean>(false);

  // 기본 이미지 경로
  const defaultCoverImage = "/placeholder-book.png";

  // 라이브러리 책 목록 조회
  const fetchLibraryBooks = async (page: number = 0) => {
    if (!libraryId) return;

    setIsLoading(true);
    try {
      // libraryService 사용하여 API 호출
      const response = await libraryService.getLibraryBooks(
        libraryId,
        page,
        pageSize
      );

      console.log(response);

      if (response.status === 200) {
        setBooks(response.data.allLibraryBooks);
        setLibraryInfo(response.data.libraryDto);
        setTotalCount(response.data.totalCount);

        // 총 페이지 수 계산
        const calculatedTotalPages = Math.ceil(
          response.data.totalCount / response.data.size
        );
        setTotalPages(calculatedTotalPages || 1);
        setCurrentPage(response.data.page);
      } else {
        throw new Error(
          response.message || "책 목록을 불러오는데 실패했습니다."
        );
      }
    } catch (error) {
      console.error("Error fetching library books:", error);
      toast.error("책 목록 조회 실패", {
        description: "라이브러리 책 목록을 불러오는 중 오류가 발생했습니다.",
      });
    } finally {
      setIsLoading(false);
    }
  };

  // BookCover 컴포넌트에 필요한 형태로 책 데이터 변환
  const mapLibraryBookToBookCoverProps = (book: LibraryBook) => {
    return {
      id: book.bookId,
      title: book.bookName,
      author: "저자 정보", // LibraryBook에는 저자 정보가 없으므로 임시 데이터
      // 이미지가 null이거나 빈 문자열이면 기본 이미지 사용
      cover:
        book.bookImage && book.bookImage.trim() !== ""
          ? book.bookImage
          : defaultCoverImage,
      category: "분류", // LibraryBook에는 카테고리 정보가 없으므로 임시 데이터
      rating: book.rating, // 임시 평점
      publishDate: book.bookPubDate,
      isNew: false,
      isBestseller: false,
    };
  };

  // 삭제 버튼 클릭 핸들러
  const handleDeleteClick = (bookId: number, e: React.MouseEvent) => {
    // 삭제할 책 찾기
    const bookToDelete = books.find((book) => book.bookId === bookId);
    if (bookToDelete) {
      setBookToDelete(bookToDelete);
      setOpenDeleteDialog(true);
    }
  };

  // 라이브러리에서 책 삭제
  const handleDeleteBook = async () => {
    if (!libraryId || !bookToDelete) return;

    try {
      await libraryService.removeBookFromLibrary(
        libraryId,
        bookToDelete.bookId
      );

      setOpenDeleteDialog(false);
      setBookToDelete(null);

      toast.success("책 삭제 완료", {
        description: "라이브러리에서 책이 삭제되었습니다.",
      });

      // 현재 책 목록에서 삭제된 책 제거
      setBooks(books.filter((book) => book.bookId !== bookToDelete.bookId));
    } catch (error) {
      console.error("Error deleting book:", error);
      toast.error("책 삭제 실패", {
        description: "책 삭제 중 오류가 발생했습니다.",
      });
    }
  };

  // 페이지 로드 시 책 목록 조회
  useEffect(() => {
    if (libraryId) {
      fetchLibraryBooks(0);
    }
  }, [libraryId]);

  // 페이지 변경 처리
  const handlePageChange = (page: number) => {
    fetchLibraryBooks(page);
  };

  // 라이브러리 이름 수정 다이얼로그 열기
  const openEditLibraryDialog = () => {
    if (libraryInfo) {
      setEditLibraryName(libraryInfo.libraryName);
      setOpenEditDialog(true);
    }
  };

  // 라이브러리 이름 수정
  const handleUpdateLibraryName = async () => {
    if (!libraryId) return;

    try {
      await libraryService.updateLibraryName(libraryId, editLibraryName);

      // 라이브러리 정보 업데이트
      if (libraryInfo) {
        setLibraryInfo({
          ...libraryInfo,
          libraryName: editLibraryName,
        });
      }

      setOpenEditDialog(false);
      toast.success("라이브러리 수정 완료", {
        description: "라이브러리 이름이 수정되었습니다.",
      });
    } catch (error) {
      console.error("Error updating library:", error);
      toast.error("라이브러리 수정 실패", {
        description: "라이브러리 이름 수정 중 오류가 발생했습니다.",
      });
    }
  };

  // 책 상세 페이지로 이동
  const handleBookClick = (bookId: number) => {
    navigate(`/read/${bookId}`);
  };

  // 책 우클릭 메뉴 (컨텍스트 메뉴)
  const handleContextMenu = (e: React.MouseEvent, book: LibraryBook) => {
    e.preventDefault();
    setBookToDelete(book);
    setOpenDeleteDialog(true);
  };

  // 라이브러리 목록 페이지로 돌아가기
  const navigateToLibraries = () => {
    navigate("/library");
  };

  // 로딩 상태 표시
  if (isLoading && books.length === 0) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-500">책 목록을 불러오는 중...</div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-6">
      {/* 헤더 섹션 */}
      <div className="mb-8">
        <Button
          variant="ghost"
          onClick={navigateToLibraries}
          className="mb-4 pl-0"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          라이브러리 목록으로 돌아가기
        </Button>

        <div className="flex items-center justify-between">
          <h1 className="text-xl font-bold">
            {libraryInfo?.libraryName || "라이브러리"}
          </h1>
          <Button
            variant="outline"
            onClick={openEditLibraryDialog}
            className="flex items-center"
          >
            <Edit className="mr-2 h-4 w-4" />
            이름 수정
          </Button>
        </div>
      </div>

      {/* 책 그리드 */}
      {books.length === 0 ? (
        <div className="bg-gray-50 rounded-lg p-8 text-center">
          <Book className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium mb-2">책이 없습니다</h3>
          <p className="text-gray-600 mb-4">
            아직 이 라이브러리에 추가된 책이 없습니다.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-5">
          {books.map((book) => (
            <div
              key={book.bookId}
              className="relative"
              onContextMenu={(e) => handleContextMenu(e, book)}
            >
              <BookCover
                book={mapLibraryBookToBookCoverProps(book)}
                onClick={handleBookClick}
                className="w-full"
                // 삭제 버튼 관련 props 추가
                showDeleteButton={true}
                onDelete={handleDeleteClick}
              />
            </div>
          ))}
        </div>
      )}

      {/* 페이지네이션 */}
      {books.length > 0 && totalPages > 1 && (
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

      {/* 라이브러리 이름 수정 다이얼로그 */}
      <Dialog open={openEditDialog} onOpenChange={setOpenEditDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>라이브러리 이름 수정</DialogTitle>
          </DialogHeader>
          <div className="mt-4">
            <Input
              placeholder="새 라이브러리 이름"
              value={editLibraryName}
              onChange={(e) => setEditLibraryName(e.target.value)}
            />
          </div>
          <DialogFooter className="mt-4">
            <DialogClose asChild>
              <Button variant="outline">취소</Button>
            </DialogClose>
            <Button
              onClick={handleUpdateLibraryName}
              disabled={!editLibraryName.trim()}
              className="bg-blue-600 hover:bg-blue-700"
            >
              수정하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 책 삭제 확인 다이얼로그 */}
      <Dialog open={openDeleteDialog} onOpenChange={setOpenDeleteDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>책 삭제</DialogTitle>
            <DialogDescription>
              "{bookToDelete?.bookName}"을(를) 라이브러리에서 삭제하시겠습니까?
              이 작업은 되돌릴 수 없습니다.
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

export default LibraryDetailPage;
