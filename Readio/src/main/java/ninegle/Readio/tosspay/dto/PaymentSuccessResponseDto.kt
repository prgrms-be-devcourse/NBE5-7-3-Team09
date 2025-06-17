package ninegle.Readio.tosspay.dto


/**
 * @param orderId  제품 id
 * @param amount 가격
 */

data class PaymentSuccessResponseDto(
    val orderId: String,
    val amount: Long)