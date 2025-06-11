import React from "react";
import { MessageSquare } from "lucide-react";
import { Pagination } from "@/components/ui/pagination";
import ReviewCard from "@/components/review/ReviewCard";

// 리뷰 인터페이스 정의
interface Review {
  id: number;
  email: string;
  rating: number;
  text: string;
  createdAt: string;
  updatedAt: string;
}

interface ReviewListProps {
  isLoading: boolean;
  filteredReviews: Review[];
  searchQuery: string;
  filterRating: string;
  totalPages: number;
  currentPage: number;
  onPageChange: (page: number) => void;
  currentUserEmail: string | null; // 현재 로그인한 사용자의 이메일
  onEditReview?: (reviewId: number) => void; // 리뷰 수정 핸들러
  onDeleteReview?: (reviewId: number) => void; // 리뷰 삭제 핸들러
}

const ReviewList: React.FC<ReviewListProps> = ({
  isLoading,
  filteredReviews,
  searchQuery,
  filterRating,
  totalPages,
  currentPage,
  onPageChange,
  currentUserEmail,
  onEditReview,
  onDeleteReview,
}) => {
  // 로딩 메시지
  const LoadingIndicator = () => (
    <div className="flex justify-center items-center py-12">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>
  );

  // 리뷰 없음 메시지
  const NoReviews = () => (
    <div className="flex flex-col items-center justify-center py-12 bg-gray-50 rounded-lg">
      <MessageSquare className="h-12 w-12 text-gray-400 mb-4" />
      <h3 className="text-lg font-medium mb-2">등록된 리뷰가 없습니다</h3>
      <p className="text-gray-600 mb-4 text-center max-w-md">
        {searchQuery || filterRating !== "all"
          ? "검색 조건에 맞는 리뷰가 없습니다. 다른 조건으로 검색해보세요."
          : "이 책에 대한 첫 리뷰를 작성해보세요!"}
      </p>
    </div>
  );

  if (isLoading) {
    return <LoadingIndicator />;
  }

  if (filteredReviews.length === 0) {
    return <NoReviews />;
  }

  return (
    <>
      <div className="grid grid-cols-1 gap-4">
        {filteredReviews.map((review) => (
          <ReviewCard
            key={review.id}
            id={review.id}
            username={review.email.split("@")[0]} // 이메일에서 아이디 부분만 표시
            user_id={review.email}
            rating={review.rating}
            date={new Date(review.createdAt).toISOString().split("T")[0]} // 날짜 형식 변환
            content={review.text}
            isOwner={
              currentUserEmail !== null && currentUserEmail === review.email
            } // 현재 사용자가 작성자인지 확인
            onEdit={onEditReview}
            onDelete={onDeleteReview}
          />
        ))}
      </div>

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div className="mt-8 flex justify-center">
          <Pagination
            pageCount={totalPages}
            onPageChange={onPageChange}
            currentPage={currentPage}
          />
        </div>
      )}
    </>
  );
};

export default ReviewList;
