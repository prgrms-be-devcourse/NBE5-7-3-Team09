package ninegle.Readio.tosspay.config

import ninegle.Readio.tosspay.dto.TossPaymentConfirmRequestDto
import ninegle.Readio.tosspay.dto.TossPaymentConfirmResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "tossClient", url = "https://api.tosspayments.com", configuration = [TossFeignConfig::class])
interface TossApiClient {
    @PostMapping("/v1/payments/confirm")
    fun confirmPayment( // TossPaymentConfirmResponse에 Toss에게 받은 결제 승인 응답을 객체로 매핑해 반환
        // 이미 TossFeginConfig에서 Header에서 Key 셋팅하고 내보내고 있으므로 아래 RequestHeader는 불필요
        @RequestHeader("Authorization") authHeader: String,  // Toss에서 요구하는 Basic 인증 헤더를 전달
        @RequestBody request: TossPaymentConfirmRequestDto // Toss에 보낼 JSON 본문
    ): TossPaymentConfirmResponseDto
}
