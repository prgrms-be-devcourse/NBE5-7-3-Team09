package ninegle.Readio.tosspay.service

import lombok.extern.slf4j.Slf4j
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.tosspay.config.TossApiClient
import ninegle.Readio.tosspay.dto.PaymentSuccessResponseDto
import ninegle.Readio.tosspay.dto.TossPaymentConfirmRequestDto
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.UserContextService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Slf4j
@Service
open class PaymentService (
    private val tossApiClient: TossApiClient,
    private val userContextService: UserContextService,
    private val userRepository: UserRepository)
{

    @Value("\${toss.secret-key}")
    private val secretKey: String? = null

    @Transactional
    open fun confirmPayment(tossRequest: TossPaymentConfirmRequestDto): PaymentSuccessResponseDto {
        //유저 검증

        val userId = userContextService.currentUserId ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        val userOptional = userRepository.findById(userId)
        if (userOptional.isEmpty) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND) //404
        }

        //인증 헤더 생성
        val encodedKey = Base64.getEncoder().encodeToString(("$secretKey:").toByteArray())

        // Feign 요청
        val response = tossApiClient.confirmPayment(
            "Basic $encodedKey", tossRequest
        )

        if ("DONE" != response.status) {
            throw BusinessException(ErrorCode.INVALID_REQUEST_DATA) //400
        }

        val user = userOptional.get()
        val point = tossRequest.amount

        if (point < 1) {
            throw BusinessException(ErrorCode.ZERO_AMOUNT_PAYMENT_NOT_ALLOWED) // 0원 결제 시 400
        }
        user.point = user.point + point

        //객체 생성 후 반환 코드 필요
        val ResponseDto = PaymentSuccessResponseDto(
            orderId = tossRequest.orderId,
            amount = tossRequest.amount)

        return ResponseDto
    }
}