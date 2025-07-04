package ninegle.Readio.tosspay.controller

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.tosspay.dto.PaymentSuccessResponseDto
import ninegle.Readio.tosspay.dto.TossPaymentConfirmRequestDto
import ninegle.Readio.tosspay.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Validated // Toss 결제 성공 후 백엔드에서 결제를 최종 승인하고 구독권을 부여하는 엔드포인트
class PaymentController (
    private val paymentService: PaymentService)
{

    @PostMapping("/confirm")
    fun confirm(
        @RequestBody request: @Valid TossPaymentConfirmRequestDto): ResponseEntity<BaseResponse<PaymentSuccessResponseDto>> {
        val response = paymentService.confirmPayment(request)
        return BaseResponse.ok("포인트 충전 완료", response, HttpStatus.OK)
    }
}