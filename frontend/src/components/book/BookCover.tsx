// src/components/book/BookCover.tsx
import React from "react";
import { Star, Clock, Trash2 } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

interface BookCoverProps {
  book: {
    id: number;
    title: string;
    author: string;
    cover: string;
    category: string;
    rating?: number;
    publishDate?: string;
    isNew?: boolean;
    isBestseller?: boolean;
  };
  isNewRelease?: boolean;
  onClick?: (id: number) => void;
  className?: string;
  // 삭제 버튼 관련 props 추가
  showDeleteButton?: boolean;
  onDelete?: (id: number, e: React.MouseEvent) => void;
}

const BookCover: React.FC<BookCoverProps> = ({
  book,
  isNewRelease = false,
  onClick,
  className = "",
  // 삭제 버튼 기본값 설정
  showDeleteButton = false,
  onDelete,
}) => {
  const handleClick = () => {
    if (onClick) {
      onClick(book.id);
    }
  };

  // 삭제 버튼 클릭 핸들러
  const handleDeleteClick = (e: React.MouseEvent) => {
    e.stopPropagation(); // 부모 요소의 클릭 이벤트가 발생하지 않도록 방지
    if (onDelete) {
      onDelete(book.id, e);
    }
  };

  // 플레이스홀더 이미지 옵션
  const placeholderOptions = [
    // 책 스타일 플레이스홀더 (푸른색)
    "https://placehold.co/400x600/e8eaf2/4a6fa5?text=No+Cover+Available&font=montserrat",
    // 책 스타일 플레이스홀더 (빨간색)
    "https://placehold.co/400x600/f5e6e8/9e3f3f?text=Cover+Coming+Soon&font=montserrat",
    // 책 스타일 플레이스홀더 (녹색)
    "https://placehold.co/400x600/e8f0e6/3e733f?text=No+Image+Available&font=montserrat",
  ];

  // 책 ID를 기반으로 일관된 플레이스홀더 이미지 선택 (같은 책은 항상 같은 플레이스홀더 사용)
  const getPlaceholderImage = (bookId: number) => {
    const index = bookId % placeholderOptions.length;
    return placeholderOptions[index];
  };

  // 커버 이미지 URL 결정
  // 유효한 URL이 아니거나 '/placeholder-book.png'이면 플레이스홀더 사용
  const coverImage =
    !book.cover ||
    book.cover === "/placeholder-book.png" ||
    book.cover.trim() === ""
      ? getPlaceholderImage(book.id)
      : book.cover;

  return (
    <div
      className={`flex-shrink-0 w-40 md:w-64 cursor-pointer group ${className}`}
      onClick={handleClick}
    >
      <div className="bg-white rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow duration-300 relative">
        <div className="relative">
          <img
            src={coverImage}
            alt={book.title}
            className="w-full h-96 object-cover"
            onError={(e) => {
              (e.target as HTMLImageElement).src = getPlaceholderImage(book.id);
            }}
          />

          {/* 삭제 버튼 - showDeleteButton이 true일 때만 표시 */}
          {showDeleteButton && (
            <div className="absolute top-1 right-1 z-10 opacity-0 group-hover:opacity-100 transition-opacity">
              <Button
                variant="ghost"
                size="icon"
                className="h-8 w-8 bg-white/80 hover:bg-white rounded-full shadow-sm"
                onClick={handleDeleteClick}
                title="삭제"
              >
                <Trash2 className="h-4 w-4 text-red-500" />
              </Button>
            </div>
          )}
        </div>
        <div className="p-3">
          <div className="flex items-center justify-between mb-1">
            <h3 className="font-medium text-sm line-clamp-1">{book.title}</h3>
            {isNewRelease && book.isNew && (
              <Badge className="ml-1 bg-green-500 text-xs">신간</Badge>
            )}
            {!isNewRelease && book.isBestseller && (
              <Badge className="ml-1 bg-red-500 text-xs">베스트셀러</Badge>
            )}
          </div>
          <p className="text-gray-600 text-xs">{book.author}</p>
          <div className="mt-2 flex items-center justify-between">
            <Badge variant="outline" className="text-xs">
              {book.category}
            </Badge>
            {isNewRelease ? (
              <div className="flex items-center text-xs text-gray-500">
                <Clock className="h-3 w-3" />
                <span>{book.publishDate?.slice(5)}</span>
              </div>
            ) : (
              <div className="flex items-center text-xs text-yellow-500">
                <Star className="h-3 w-3 fill-yellow-500" />
                <span>{book.rating}</span>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookCover;
