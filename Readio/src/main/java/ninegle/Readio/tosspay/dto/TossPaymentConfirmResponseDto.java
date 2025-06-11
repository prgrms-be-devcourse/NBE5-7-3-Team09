package ninegle.Readio.tosspay.dto;

/**
 * @param status  결제 상태 "DONE" */

public record TossPaymentConfirmResponseDto(String orderId, String paymentKey, String status, String requestedAt,
											String approvedAt, int totalAmount) {
}
