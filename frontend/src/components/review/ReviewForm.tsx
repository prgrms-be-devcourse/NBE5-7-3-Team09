import React from "react";
import { Star, MessageSquare } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";

interface ReviewFormProps {
  isAuthenticated: boolean;
  userRating: number | null;
  setUserRating: (rating: number | null) => void;
  reviewContent: string;
  setReviewContent: (content: string) => void;
  handleSubmitReview: () => void;
  navigate: (path: string) => void;
}

const ReviewForm: React.FC<ReviewFormProps> = ({
  isAuthenticated,
  userRating,
  setUserRating,
  reviewContent,
  setReviewContent,
  handleSubmitReview,
  navigate,
}) => {
  // 별점을 시각적으로 표시하는 컴포넌트
  const RatingStars = () => {
    // 별점 선택 (0.5점 단위로 지원)
    const handleStarClick = (
      e: React.MouseEvent<HTMLDivElement>,
      rating: number
    ) => {
      e.preventDefault();

      // 버튼 내에서 클릭 위치 계산
      const rect = e.currentTarget.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const halfPoint = rect.width / 2;

      // 왼쪽 절반을 클릭하면 .5점, 오른쪽 절반을 클릭하면 정수점
      let newRating = x < halfPoint ? rating - 0.5 : rating;

      // 같은 값 클릭 시 별점 선택 취소 (토글)
      if (userRating === newRating) {
        setUserRating(null);
      } else {
        setUserRating(newRating);
      }
    };

    return (
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map((rating) => (
          <div
            key={rating}
            className="relative p-1 cursor-pointer"
            onClick={(e) => handleStarClick(e, rating)}
          >
            {/* 기본 별 (비어있는 상태) */}
            <Star className="h-8 w-8 text-gray-300" />

            {/* 꽉 찬 별 (선택된 상태) */}
            {userRating !== null && rating <= Math.floor(userRating) && (
              <Star className="h-8 w-8 text-yellow-500 fill-yellow-500 absolute top-1 left-1" />
            )}

            {/* 반쪽 별 (0.5점 단위일 때) */}
            {userRating !== null &&
              rating === Math.ceil(userRating) &&
              !Number.isInteger(userRating) && (
                <div className="h-8 w-4 overflow-hidden absolute top-1 left-1">
                  <Star className="h-8 w-8 text-yellow-500 fill-yellow-500" />
                </div>
              )}
          </div>
        ))}
        <span className="ml-2 flex items-center text-gray-600">
          {userRating !== null ? `${userRating}점` : "별점을 선택해주세요"}
        </span>
      </div>
    );
  };

  // 로그인 필요 메시지
  const LoginRequired = () => (
    <div className="bg-gray-50 p-4 rounded-lg flex flex-col items-center justify-center py-6">
      <MessageSquare className="h-10 w-10 text-gray-400 mb-3" />
      <h3 className="text-lg font-medium mb-2">로그인이 필요합니다</h3>
      <p className="text-gray-600 text-center mb-4">
        리뷰를 작성하려면 먼저 로그인해주세요.
      </p>
      <Button
        onClick={() => navigate("/login")}
        className="bg-blue-600 hover:bg-blue-700"
      >
        로그인하기
      </Button>
    </div>
  );

  // 리뷰 내용 변경 핸들러(로컬 함수로 정의)
  const handleReviewContentChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    e.preventDefault();
    const value = e.target.value;
    setReviewContent(value);
  };

  return (
    <Card className="h-full flex flex-col">
      <CardContent className="pt-6 flex-1 flex flex-col">
        <h2 className="text-xl font-bold mb-4">리뷰 작성</h2>

        {!isAuthenticated ? (
          <LoginRequired />
        ) : (
          <>
            {/* 별점 선택 */}
            <div className="mb-4">
              <p className="text-sm text-gray-600 mb-2">
                별점 (별의 왼쪽을 클릭하면 0.5점, 오른쪽을 클릭하면 1점)
              </p>
              <RatingStars />
            </div>

            {/* 리뷰 내용 */}
            <div className="flex flex-col flex-1">
              <p className="text-sm text-gray-600 mb-2">리뷰 내용</p>
              <Textarea
                placeholder="이 책에 대한 생각을 자유롭게 작성해주세요."
                className="flex-1 mb-4 min-h-[120px] resize-none"
                value={reviewContent}
                onChange={handleReviewContentChange}
              />
              <div className="flex justify-end">
                <Button
                  onClick={handleSubmitReview}
                  className="bg-blue-600 hover:bg-blue-700"
                  disabled={!userRating || !reviewContent.trim()}
                >
                  리뷰 등록하기
                </Button>
              </div>
            </div>
          </>
        )}
      </CardContent>
    </Card>
  );
};

export default ReviewForm;
