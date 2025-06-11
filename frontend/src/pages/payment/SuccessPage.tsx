import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios, { AxiosError } from "axios";

export function SuccessPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const orderId = searchParams.get("orderId");
    const amount = searchParams.get("amount");
    const paymentKey = searchParams.get("paymentKey");

    if (!orderId || !amount || !paymentKey) {
      alert("❌ 필수 결제 정보가 누락되었습니다.");
      return;
    }

    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }

    let alreadyConfirmed = false;

    const confirm = async () => {
      if (alreadyConfirmed) return;
      alreadyConfirmed = true;

      try {
        const response = await axios.post(
          `${import.meta.env.VITE_API_BASE_URL}/api/payments/confirm`,
          { orderId, amount, paymentKey },
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        console.log("📦 요청 보냄:", { orderId, amount, paymentKey });
        console.log("🔄 응답 상태:", response.status);
        console.log("✅ 결제 성공:", response.data);

        // 성공 시 마이페이지로 이동
        navigate("/my-page", {
          state: {
            success: true,
            message: "포인트가 성공적으로 충전되었습니다.",
          },
        });

        // 이곳에서 포인트 적립 등 후속 처리를 추가할 수 있음
      } catch (error: unknown) {
        console.error("❌ 서버 요청 실패:", error);

        // axios 에러 타입으로 변환하여 처리
        const err = error as AxiosError;

        if (err.response) {
          // 서버가 응답을 반환했지만 2xx 범위가 아닌 경우
          console.error("❌ 결제 실패 응답:", err.response.data);
          navigate(
            `/fail?message=${encodeURIComponent("결제확인실패")}&code=${
              err.response.status
            }`
          );
        } else if (err.request) {
          // 요청은 보냈지만 응답을 받지 못한 경우
          console.error("❌ 응답을 받지 못함:", err.request);
          navigate(
            `/fail?message=${encodeURIComponent("서버응답없음")}&code=0`
          );
        } else {
          // 요청 설정 중에 오류가 발생한 경우
          navigate(`/fail?message=${encodeURIComponent("서버오류")}&code=500`);
        }
      }
    };

    const timer = setTimeout(confirm, 500);
    return () => clearTimeout(timer);
  }, [navigate, searchParams]);

  return (
    <div className="container mx-auto max-w-md py-10">
      <div className="bg-white shadow-md rounded-lg p-6 text-center">
        <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-8 w-8 text-green-600"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M5 13l4 4L19 7"
            />
          </svg>
        </div>
        <h2 className="text-2xl font-bold text-gray-800 mb-4">결제 성공</h2>
        <div className="space-y-2 mb-6">
          <p className="text-gray-600">{`주문번호: ${searchParams.get(
            "orderId"
          )}`}</p>
          <p className="text-gray-600">{`결제 금액: ${Number(
            searchParams.get("amount") || "0"
          ).toLocaleString()}원`}</p>
        </div>
        <button
          onClick={() => navigate("/my-page")}
          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-6 rounded-lg w-full"
        >
          마이페이지로 이동
        </button>
      </div>
    </div>
  );
}

export default SuccessPage;
