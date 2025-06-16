package ninegle.Readio.tosspay.dto

/**
 * @param status  결제 상태 "DONE"
 */
@JvmRecord
data class TossPaymentConfirmResponseDto(
	val orderId: String,
	val paymentKey: String,
	val status: String,
	val requestedAt: String,
	val approvedAt: String,
	val totalAmount: Int
)
