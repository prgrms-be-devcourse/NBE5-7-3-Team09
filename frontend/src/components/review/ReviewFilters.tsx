import React from "react";
import { Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

interface ReviewFiltersProps {
  totalReviews: number;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  filterRating: string;
  setFilterRating: (rating: string) => void;
  sortBy: string;
  setSortBy: (sort: string) => void;
}

const ReviewFilters: React.FC<ReviewFiltersProps> = ({
  totalReviews,
  searchQuery,
  setSearchQuery,
  filterRating,
  setFilterRating,
  sortBy,
  setSortBy,
}) => {
  return (
    <div className="flex flex-col md:flex-row justify-between items-center mb-6 gap-4">
      <h2 className="text-xl font-bold">독자 리뷰 ({totalReviews})</h2>

      <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
        <div className="relative flex-1 sm:w-64">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
          <Input
            placeholder="리뷰 검색..."
            className="pl-9 pr-4 h-10"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div className="flex gap-2">
          <Select
            value={filterRating}
            onValueChange={(value) => setFilterRating(value)}
          >
            <SelectTrigger className="w-[120px] h-10">
              <SelectValue placeholder="별점" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">모든 별점</SelectItem>
              <SelectItem value="5">5점</SelectItem>
              <SelectItem value="4">4점</SelectItem>
              <SelectItem value="3">3점</SelectItem>
              <SelectItem value="2">2점</SelectItem>
              <SelectItem value="1">1점</SelectItem>
            </SelectContent>
          </Select>

          <Select value={sortBy} onValueChange={(value) => setSortBy(value)}>
            <SelectTrigger className="w-[120px] h-10">
              <SelectValue placeholder="정렬 기준" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="recent">최신순</SelectItem>
              <SelectItem value="highest">별점 높은순</SelectItem>
              <SelectItem value="lowest">별점 낮은순</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>
    </div>
  );
};

export default ReviewFilters;
