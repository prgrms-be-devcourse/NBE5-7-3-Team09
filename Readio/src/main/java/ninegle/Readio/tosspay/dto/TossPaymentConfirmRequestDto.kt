package ninegle.Readio.tosspay.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@JvmRecord
data class TossPaymentConfirmRequestDto(
	@NotBlank(message = "결제 키(paymentKey)는 필수 입력값입니다.")
	val paymentKey:String = "",

	@NotBlank(message = "주문 번호(orderId)는 필수 입력값입니다.")
	val orderId: String = "",

	@NotNull(message = "결제 키(paymentKey)는 필수 입력값입니다.")
	val amount: Long
)
