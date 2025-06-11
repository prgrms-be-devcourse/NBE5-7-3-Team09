package ninegle.Readio.subscription.mapper;

import org.springframework.stereotype.Component;

import ninegle.Readio.subscription.domain.Subscription;
import ninegle.Readio.subscription.dto.response.SubscriptionResponseDto;

@Component
public class SubscriptionMapper {

	public SubscriptionResponseDto toDto(Subscription subscription) {
		return SubscriptionResponseDto.builder()
			.userId(subscription.getUserId())
			.subDate(subscription.getSubDate())
			.expDate(subscription.getExpDate())
			.active(subscription.isActive())
			.canceled(subscription.isCanceled())
			.build();
	}
}