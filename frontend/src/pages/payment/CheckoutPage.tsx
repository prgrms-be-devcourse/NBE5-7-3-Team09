import { useEffect, useRef } from "react";
import {
  loadPaymentWidget,
  type PaymentWidgetInstance,
} from "@tosspayments/payment-widget-sdk";
import { useLocation, useNavigate } from "react-router-dom";
import { nanoid } from "nanoid";
import { Card, CardContent } from "@/components/ui/card";

const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm"; // 토스 테스트 키
const customerKey = "customer123";

export default function CheckoutPage() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const amount = state?.amount || 0;

  const paymentWidgetRef = useRef<PaymentWidgetInstance | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const paymentWidget = await loadPaymentWidget(clientKey, customerKey);
        paymentWidgetRef.current = paymentWidget;

        await paymentWidget.renderPaymentMethods("#payment-method", {
          value: amount,
        });
        await paymentWidget.renderAgreement("#agreement");
      } catch (err) {
        console.error("❌ Toss 위젯 로딩 실패:", err);
      }
    })();
  }, [amount]);

  const handlePayment = async () => {
    const paymentWidget = paymentWidgetRef.current;

    if (!paymentWidget) {
      alert("결제 위젯이 아직 초기화되지 않았습니다.");
      return;
    }

    if (!amount || amount < 1000) {
      alert("충전 금액이 올바르지 않습니다.");
      return;
    }

    try {
      await paymentWidget.requestPayment({
        orderId: nanoid(),
        orderName: "포인트 충전",
        successUrl: `${window.location.origin}/success`,
        failUrl: `${window.location.origin}/fail`,
      });
    } catch (error) {
      console.error("❌ 결제 요청 에러:", error);
      alert("결제 요청 중 문제가 발생했습니다.");
    }
  };

  const handleCancel = () => {
    // 이전 페이지로 돌아가기
    navigate(-1);
  };

  return (
    <div className="container mx-auto ">
      {/* 결제 요약 및 헤더 */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold mb-4">결제 정보</h1>

        <div className="bg-gray-50 p-4 rounded-lg mb-6 border border-gray-200">
          <div className="flex justify-between items-center mb-2">
            <span className="text-gray-600">주문 내용</span>
            <span className="font-medium">포인트 충전</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-gray-600">결제 금액</span>
            <span className="text-xl font-bold text-blue-600">
              {amount.toLocaleString()}원
            </span>
          </div>
        </div>
      </div>

      <h1 className="text-lg mb-4 font-semibold">결제 수단 선택</h1>
      <Card className="mb-4 !p-1">
        <CardContent className="!p-0">
          <div id="payment-method" />
        </CardContent>
      </Card>

      {/* 이용 약관 동의 */}
      <h2 className="text-lg font-semibold mb-4">이용 약관 동의</h2>
      <Card className="mb-4 !p-1">
        <CardContent className="!p-0">
          <div id="agreement" />
        </CardContent>
      </Card>

      {/* 결제 버튼 - 고정된 위치에 배치 */}
      <div className="sticky bottom-0 bg-white pt-4 pb-8 border-t border-gray-200 mt-8">
        <div className="flex flex-col space-y-2 sm:flex-row sm:space-y-0 sm:space-x-4">
          <button
            onClick={handleCancel}
            className="py-3 px-6 rounded-lg border border-gray-300 text-gray-700 font-semibold hover:bg-gray-50 sm:flex-1 cursor-pointer"
          >
            취소
          </button>
          <button
            onClick={handlePayment}
            className="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-3 px-6 rounded-lg sm:flex-1 cursor-pointer"
          >
            {amount.toLocaleString()}원 결제하기
          </button>
        </div>
      </div>
    </div>
  );
}
