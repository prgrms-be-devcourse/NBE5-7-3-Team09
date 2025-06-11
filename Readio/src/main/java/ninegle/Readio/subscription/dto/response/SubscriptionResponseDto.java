package ninegle.Readio.subscription.dto.response;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record SubscriptionResponseDto(
	Long userId,
	LocalDate subDate,
	LocalDate expDate,
	boolean active,
	boolean canceled) {
}