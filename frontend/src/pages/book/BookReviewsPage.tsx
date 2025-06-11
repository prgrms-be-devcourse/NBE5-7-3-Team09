// 수정된 BookReviewsPage.tsx
import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Star } from "lucide-react";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { useAuth } from "@/contexts/AuthContext";
import BookCover from "@/components/book/BookCover";
import ReviewList from "@/components/review/ReviewList";
import ReviewFilters from "@/components/review/ReviewFilters";
import ReviewForm from "@/components/review/ReviewForm";
import reviewService, { Review } from "@/utils/api/reviewService";
import bookService from "@/utils/api/bookService";
import { BookDetail } from "@/types/book";
import {
  Dialog,
  DialogFooter,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";

const BookReviewsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated, email } = useAuth();

  // 상태 관리
  const [book, setBook] = useState<BookDetail | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [filteredReviews, setFilteredReviews] = useState<Review[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingBook, setIsLoadingBook] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalReviews, setTotalReviews] = useState(0);
  const [averageRating, setAverageRating] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterRating, setFilterRating] = useState<string>("all");
  const [sortBy, setSortBy] = useState<string>("recent");
  const [reviewContent, setReviewContent] = useState("");
  const [userRating, setUserRating] = useState<number | null>(null);
  const [pageSize] = useState(6);
  const [error, setError] = useState<string | null>(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editReviewId, setEditReviewId] = useState<number | null>(null);
  const [editRating, setEditRating] = useState<number | null>(null);
  const [editContent, setEditContent] = useState("");

  // 요청 상태 관리용 Refs
  const loadingRequestRef = useRef(false);
  const submitRequestRef = useRef(false);
  const requestControllerRef = useRef<AbortController | null>(null);

  // 리뷰 데이터 로드 함수 - 중복 요청 방지 로직 추가
  const loadReviews = async (page = 1) => {
    // 이미 요청 중이면 중복 요청 방지
    if (loadingRequestRef.current) {
      console.log("이미 요청 진행 중입니다.");
      return;
    }

    // 이전 요청이 있으면 취소
    if (requestControllerRef.current) {
      requestControllerRef.current.abort();
    }

    // 새 요청을 위한 AbortController 생성
    requestControllerRef.current = new AbortController();

    loadingRequestRef.current = true;
    setIsLoading(true);

    try {
      // reviewService를 사용하여 리뷰 데이터 로드
      const response = await reviewService.getBookReviews(id, page, pageSize);
      const { reviews, pagination, summary } = response.data;

      setReviews(reviews);
      setFilteredReviews(reviews);
      setTotalPages(pagination.totalPages || 1);
      setTotalReviews(summary.totalReviews);
      setAverageRating(summary.averageRating);
    } catch (error) {
      // AbortError는 무시 (페이지 전환 등으로 인한 정상적인 취소)
      if (error instanceof Error && error.name === "AbortError") {
        console.log("리뷰 요청이 취소되었습니다.");
        return;
      }

      console.error("리뷰 데이터 로드 중 오류 발생:", error);
      toast.error("리뷰를 불러오는 데 실패했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      });
    } finally {
      // 요청 상태 초기화
      loadingRequestRef.current = false;
      setIsLoading(false);
    }
  };

  // 책 정보 로드 함수
  const loadBookInfo = async () => {
    if (!id) return;

    setIsLoadingBook(true);
    try {
      // bookService를 사용해 책 정보 가져오기
      const response = await bookService.getBookDetail(id);
      setBook(response.data.data);
    } catch (error) {
      console.error("책 정보 로드 중 오류 발생:", error);
      setError(
        "책 정보를 불러오는 데 실패했습니다. 잠시 후 다시 시도해주세요."
      );
      toast.error("책 정보를 불러오는 데 실패했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      });
    } finally {
      setIsLoadingBook(false);
    }
  };

  // 컴포넌트 마운트/언마운트 관리
  useEffect(() => {
    let isMounted = true;

    // 초기 데이터 로드
    if (id) {
      loadBookInfo();
      loadReviews(1);
    }

    // 클린업 함수
    return () => {
      isMounted = false;

      // 진행 중인 요청 취소
      if (requestControllerRef.current) {
        requestControllerRef.current.abort();
      }
    };
  }, [id]);

  // 현재 페이지가 변경되면 리뷰 데이터를 다시 로드
  useEffect(() => {
    // 최초 로딩 시에는 중복 호출 방지
    if (id && !isLoading && currentPage > 0) {
      loadReviews(currentPage);
    }
  }, [currentPage]);

  // 필터링 로직
  useEffect(() => {
    if (!reviews.length) return;

    let filtered = [...reviews];

    // 검색어 필터링
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      filtered = filtered.filter(
        (review) =>
          review.text.toLowerCase().includes(query) ||
          review.email.toLowerCase().includes(query)
      );
    }

    // 별점 필터링
    if (filterRating !== "all") {
      const rating = parseInt(filterRating);
      filtered = filtered.filter(
        (review) => Math.floor(review.rating) === rating
      );
    }

    // 정렬
    filtered.sort((a, b) => {
      if (sortBy === "recent") {
        return (
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
      } else if (sortBy === "highest") {
        return b.rating - a.rating;
      } else {
        // lowest
        return a.rating - b.rating;
      }
    });

    setFilteredReviews(filtered);
  }, [reviews, searchQuery, filterRating, sortBy]);

  // 뒤로 가기
  const handleGoBack = () => {
    navigate(`/book/${id}`);
  };

  // 페이지 변경
  const handlePageChange = (page: number) => {
    if (page !== currentPage) {
      setCurrentPage(page);
    }
  };

  // 리뷰 제출 - 중복 제출 방지 추가
  const handleSubmitReview = async () => {
    // 이미 제출 중이면 중복 요청 방지
    if (submitRequestRef.current) {
      return;
    }

    const accessToken = localStorage.getItem("accessToken");

    if (!isAuthenticated || !accessToken) {
      toast.error("로그인이 필요합니다", {
        description: "리뷰를 작성하려면 먼저 로그인해주세요.",
      });
      return;
    }

    if (!userRating) {
      toast.error("별점을 선택해주세요", {
        description: "리뷰 작성 시 별점은 필수입니다.",
      });
      return;
    }

    if (!reviewContent.trim()) {
      toast.error("리뷰 내용을 입력해주세요", {
        description: "리뷰 내용은 필수입니다.",
      });
      return;
    }

    submitRequestRef.current = true;

    try {
      const reviewData = {
        rating: userRating,
        text: reviewContent,
      };

      // reviewService를 사용하여 리뷰 작성
      await reviewService.createReview(id, reviewData, accessToken);

      // 리뷰 등록 후 폼 초기화
      setReviewContent("");
      setUserRating(null);

      // 리뷰 목록 새로고침
      loadReviews(currentPage);

      // 성공 메시지
      toast.success("리뷰가 등록되었습니다", {
        description: "소중한 의견을 공유해주셔서 감사합니다.",
      });
    } catch (error) {
      console.error("리뷰 등록 중 오류 발생:", error);
      toast.error("리뷰 등록에 실패했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      });
    } finally {
      submitRequestRef.current = false;
    }
  };

  // 리뷰 수정 모달 열기
  const handleEditReview = (reviewId: number) => {
    // 해당 리뷰 찾기
    const reviewToEdit = reviews.find((review) => review.id === reviewId);

    if (reviewToEdit) {
      setEditReviewId(reviewId);
      setEditRating(reviewToEdit.rating);
      setEditContent(reviewToEdit.text);
      setIsEditModalOpen(true);
    }
  };

  // 리뷰 수정 제출
  const handleUpdateReview = async () => {
    if (!editReviewId || !editRating) return;

    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
      toast.error("로그인이 필요합니다");
      return;
    }

    try {
      const reviewData = {
        rating: editRating,
        text: editContent,
      };

      await reviewService.updateReview(
        id,
        editReviewId,
        reviewData,
        accessToken
      );

      // 수정 후 리뷰 목록 새로고침
      loadReviews(currentPage);

      // 모달 닫기 및 상태 초기화
      setIsEditModalOpen(false);
      setEditReviewId(null);
      setEditRating(null);
      setEditContent("");

      toast.success("리뷰가 수정되었습니다");
    } catch (error) {
      console.error("리뷰 수정 중 오류 발생:", error);
      toast.error("리뷰 수정에 실패했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      });
    }
  };

  // 리뷰 삭제
  const handleDeleteReview = async (reviewId: number) => {
    if (!window.confirm("정말로 이 리뷰를 삭제하시겠습니까?")) {
      return;
    }

    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
      toast.error("로그인이 필요합니다");
      return;
    }

    try {
      await reviewService.deleteReview(id, reviewId, accessToken);

      // 삭제 후 리뷰 목록 새로고침
      loadReviews(currentPage);

      toast.success("리뷰가 삭제되었습니다");
    } catch (error) {
      console.error("리뷰 삭제 중 오류 발생:", error);
      toast.error("리뷰 삭제에 실패했습니다", {
        description: "잠시 후 다시 시도해주세요.",
      });
    }
  };

  // 책 이미지 플레이스홀더
  const bookImage =
    book?.image ||
    "https://placehold.co/400x600/e8eaf2/4a6fa5?text=No+Cover+Available&font=montserrat";

  // 로딩 중 표시
  if (isLoadingBook) {
    return (
      <div className="container mx-auto py-12 flex flex-col items-center justify-center">
        <div className="w-12 h-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
        <p className="mt-4 text-gray-600">책 정보를 불러오는 중...</p>
      </div>
    );
  }

  // 에러 표시
  if (error || !book) {
    return (
      <div className="container mx-auto py-12 flex flex-col items-center justify-center">
        <div className="bg-red-100 p-6 rounded-lg text-center max-w-md">
          <h2 className="text-xl font-semibold text-red-700 mb-2">오류 발생</h2>
          <p className="text-gray-700">
            {error || "책 정보를 불러올 수 없습니다."}
          </p>
          <Button className="mt-4" onClick={() => navigate(-1)}>
            뒤로 가기
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto">
      {/* 헤더 */}
      <div className="mb-6">
        <Button variant="ghost" onClick={handleGoBack} className="pl-0">
          <ArrowLeft className="h-5 w-5 mr-2" />
          도서 상세로 돌아가기
        </Button>
      </div>

      {/* 책 정보와 리뷰 폼 레이아웃 */}
      <div className="grid grid-cols-1 md:grid-cols-5 gap-4 md:gap-6 mb-8">
        {/* 좌측: 책 정보 */}
        <div className="md:col-span-1">
          <div className="flex flex-col items-center md:items-start">
            <BookCover
              book={{
                id: Number(id),
                title: book.name,
                author: book.author.name,
                cover: bookImage,
                category: book.category.major,
                rating: averageRating || 0,
              }}
              className="mx-auto md:mx-0 w-full max-w-[180px] md:max-w-full"
            />
          </div>
        </div>

        {/* 우측: 리뷰 작성 폼 - 분리된 컴포넌트 사용 */}
        <div className="md:col-span-4">
          <ReviewForm
            isAuthenticated={isAuthenticated}
            userRating={userRating}
            setUserRating={setUserRating}
            reviewContent={reviewContent}
            setReviewContent={setReviewContent}
            handleSubmitReview={handleSubmitReview}
            navigate={navigate}
          />
        </div>
      </div>

      {/* 리뷰 필터 및 검색 - 분리된 컴포넌트 사용 */}
      <ReviewFilters
        totalReviews={totalReviews}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        filterRating={filterRating}
        setFilterRating={setFilterRating}
        sortBy={sortBy}
        setSortBy={setSortBy}
      />

      {/* 리뷰 목록 - 분리된 컴포넌트 사용 */}
      <ReviewList
        isLoading={isLoading}
        filteredReviews={filteredReviews}
        searchQuery={searchQuery}
        filterRating={filterRating}
        totalPages={totalPages}
        currentPage={currentPage}
        onPageChange={handlePageChange}
        currentUserEmail={email || ""}
        // 현재 사용자 이메일 전달
        onEditReview={handleEditReview} // 수정 핸들러 전달
        onDeleteReview={handleDeleteReview}
      />
      {isEditModalOpen && (
        <Dialog open={isEditModalOpen} onOpenChange={setIsEditModalOpen}>
          <DialogContent className="sm:max-w-[425px]">
            <DialogHeader>
              <DialogTitle>리뷰 수정</DialogTitle>
              <DialogDescription>
                아래 내용을 수정한 후 저장 버튼을 클릭하세요.
              </DialogDescription>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div className="flex items-center justify-center space-x-2">
                {[1, 2, 3, 4, 5].map((star) => (
                  <button
                    key={star}
                    type="button"
                    onClick={() => setEditRating(star)}
                    className="focus:outline-none"
                  >
                    <Star
                      className={`h-8 w-8 ${
                        editRating && star <= editRating
                          ? "text-yellow-500 fill-yellow-500"
                          : "text-gray-300"
                      }`}
                    />
                  </button>
                ))}
              </div>
              <div className="grid gap-2">
                <Label htmlFor="edit-review">리뷰 내용</Label>
                <Textarea
                  id="edit-review"
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows={5}
                  placeholder="리뷰 내용을 입력하세요..."
                  className="resize-none"
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                variant="outline"
                onClick={() => setIsEditModalOpen(false)}
              >
                취소
              </Button>
              <Button onClick={handleUpdateReview}>저장</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      )}
    </div>
  );
};

export default BookReviewsPage;
