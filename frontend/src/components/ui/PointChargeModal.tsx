import React, { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  totalCharge: number;
  setTotalCharge: (amount: number) => void;
  userPoint: number;
  onSubmit: () => void;
}

export default function PointChargeModal({
  isOpen,
  onClose,
  totalCharge,
  setTotalCharge,
  onSubmit,
  userPoint,
}: Props) {
  // 직접 입력용 상태
  const [inputAmount, setInputAmount] = useState<string>("");

  // 직접 입력 처리
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // 숫자만 입력 가능하도록
    const value = e.target.value.replace(/[^0-9]/g, "");
    setInputAmount(value);
  };

  // 직접 입력 금액 적용
  const applyInputAmount = () => {
    const amount = parseInt(inputAmount, 10);
    if (!isNaN(amount) && amount >= 0) {
      // 5,000원 단위로 반올림
      const roundedAmount = Math.round(amount / 5000) * 5000;
      setTotalCharge(roundedAmount);
      setInputAmount("");
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-center text-xl">포인트 충전</DialogTitle>
          <DialogDescription className="text-center">
            충전할 포인트 금액을 선택해주세요.<br></br>
            5,000원 또는 10,000원 단위
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6 py-4">
          {/* 현재 선택된 금액과 충전 후 예상 포인트 표시 */}
          <div className="bg-blue-50 p-4 rounded-lg">
            <div className="flex justify-between items-center mb-2">
              <span className="text-gray-600">충전 금액:</span>
              <span className="text-lg font-semibold text-blue-700">
                {totalCharge.toLocaleString()}원
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-600">충전 후 예상 포인트:</span>
              <span className="text-lg font-semibold text-blue-700">
                {(userPoint + totalCharge).toLocaleString()}P
              </span>
            </div>
          </div>

          {/* 금액 컨트롤 */}
          <div className="space-y-4">
            {/* 직접 입력 필드 */}
            <div className="flex items-center gap-2 mt-4">
              <Input
                type="text"
                placeholder="직접 입력 (원)"
                value={inputAmount}
                onChange={handleInputChange}
                className="flex-1"
              />
              <Button variant="secondary" onClick={applyInputAmount}>
                적용
              </Button>
            </div>
          </div>
        </div>

        <DialogFooter className="flex justify-end gap-2">
          <Button variant="outline" onClick={onClose}>
            취소
          </Button>
          <Button
            onClick={onSubmit}
            disabled={totalCharge === 0}
            className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            충전하기
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
