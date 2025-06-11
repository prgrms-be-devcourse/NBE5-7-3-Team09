package ninegle.Readio.tosspay.dto;

import lombok.Builder;

/**
 * @param orderId  제품 id
 * @param amount 가격 */

@Builder
public record PaymentSuccessResponseDto(String orderId, long amount) {
}