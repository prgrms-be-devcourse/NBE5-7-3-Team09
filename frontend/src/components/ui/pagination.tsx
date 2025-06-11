// src/components/ui/pagination.tsx
import React from "react";
import { ChevronLeft, ChevronRight, MoreHorizontal } from "lucide-react";
import { Button } from "@/components/ui/button";

interface PaginationProps {
  pageCount: number;
  currentPage: number;
  onPageChange: (page: number) => void;
  siblingCount?: number;
}

export const Pagination: React.FC<PaginationProps> = ({
  pageCount,
  currentPage,
  onPageChange,
  siblingCount = 1,
}) => {
  // 페이지 번호 범위 생성 함수 (시작 페이지, 끝 페이지)
  const range = (start: number, end: number) => {
    const length = end - start + 1;
    return Array.from({ length }, (_, idx) => idx + start);
  };

  // 표시할 페이지 번호 계산
  const generatePagination = () => {
    // 전체 페이지가 7 이하면 모든 페이지 표시
    if (pageCount <= 7) {
      return range(1, pageCount);
    }

    // 첫 페이지와 마지막 페이지는 항상 표시
    const firstPageIndex = 1;
    const lastPageIndex = pageCount;

    // 현재 페이지 주변 페이지 표시 범위 계산
    const leftSiblingIndex = Math.max(currentPage - siblingCount, 1);
    const rightSiblingIndex = Math.min(currentPage + siblingCount, pageCount);

    // 생략 부호 표시 여부
    const shouldShowLeftDots = leftSiblingIndex > 2;
    const shouldShowRightDots = rightSiblingIndex < pageCount - 1;

    // 다양한 케이스에 따른 페이지 번호 배열 생성
    if (!shouldShowLeftDots && shouldShowRightDots) {
      // 왼쪽 생략 없음, 오른쪽 생략 있음
      const leftItemCount = 3 + 2 * siblingCount;
      const leftRange = range(1, leftItemCount);
      return [...leftRange, "dots", pageCount];
    }

    if (shouldShowLeftDots && !shouldShowRightDots) {
      // 왼쪽 생략 있음, 오른쪽 생략 없음
      const rightItemCount = 3 + 2 * siblingCount;
      const rightRange = range(pageCount - rightItemCount + 1, pageCount);
      return [firstPageIndex, "dots", ...rightRange];
    }

    if (shouldShowLeftDots && shouldShowRightDots) {
      // 양쪽 모두 생략 있음
      const middleRange = range(leftSiblingIndex, rightSiblingIndex);
      return [firstPageIndex, "dots", ...middleRange, "dots", lastPageIndex];
    }
  };

  const pages = generatePagination();

  return (
    <nav className="flex justify-center">
      <ul className="flex items-center gap-1">
        {/* 이전 페이지 버튼 */}
        <li>
          <Button
            variant="outline"
            size="icon"
            className="h-8 w-8"
            onClick={() => onPageChange(Math.max(1, currentPage - 1))}
            disabled={currentPage === 1}
          >
            <ChevronLeft className="h-4 w-4" />
          </Button>
        </li>

        {/* 페이지 번호 */}
        {pages?.map((page, index) => {
          if (page === "dots") {
            return (
              <li key={`dots-${index}`}>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8 cursor-default"
                  disabled
                >
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </li>
            );
          }

          return (
            <li key={`page-${page}`}>
              <Button
                variant={currentPage === page ? "default" : "outline"}
                size="icon"
                className={`h-8 w-8 ${
                  currentPage === page
                    ? "bg-blue-600 hover:bg-blue-700"
                    : "hover:bg-gray-100"
                }`}
                onClick={() => onPageChange(page as number)}
              >
                {page}
              </Button>
            </li>
          );
        })}

        {/* 다음 페이지 버튼 */}
        <li>
          <Button
            variant="outline"
            size="icon"
            className="h-8 w-8"
            onClick={() => onPageChange(Math.min(pageCount, currentPage + 1))}
            disabled={currentPage === pageCount}
          >
            <ChevronRight className="h-4 w-4" />
          </Button>
        </li>
      </ul>
    </nav>
  );
};
