import React from "react";
import { Star, Edit2, Trash2 } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

export interface ReviewCardProps {
  id: number;
  username: string;
  user_id?: string;
  rating: number;
  date: string;
  content: string;
  className?: string;
  isOwner?: boolean;
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

const ReviewCard: React.FC<ReviewCardProps> = ({
  id,
  username,
  rating,
  date,
  content,
  className = "",
  isOwner = false,
  onEdit,
  onDelete,
}) => {
  return (
    <Card className={`overflow-hidden ${className}`}>
      <CardContent>
        <div className="flex justify-between items-start">
          <div className="flex items-center">
            <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-medium">
              {username.charAt(0)}
            </div>
            <div className="ml-3">
              <p className="font-medium">{username}</p>
              <p className="text-sm text-gray-500">{date}</p>
            </div>
          </div>
          <div className="flex items-center">
            <div className="flex items-center bg-yellow-50 px-2 py-1 rounded-full mr-2">
              <Star className="h-4 w-4 fill-yellow-500 text-yellow-500" />
              <span className="ml-1 text-sm font-medium text-yellow-700">
                {rating}
              </span>
            </div>

            {/* 리뷰 작성자인 경우에만 수정/삭제 버튼 표시 */}
            {isOwner && (
              <div className="flex space-x-1">
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-8 w-8 p-0 rounded-full"
                  onClick={() => onEdit && onEdit(id)}
                  title="리뷰 수정"
                >
                  <Edit2 className="h-4 w-4 text-blue-600" />
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-8 w-8 p-0 rounded-full"
                  onClick={() => onDelete && onDelete(id)}
                  title="리뷰 삭제"
                >
                  <Trash2 className="h-4 w-4 text-red-500" />
                </Button>
              </div>
            )}
          </div>
        </div>
        <p className="mt-4 text-gray-700 whitespace-pre-line">{content}</p>
      </CardContent>
    </Card>
  );
};

export default ReviewCard;
