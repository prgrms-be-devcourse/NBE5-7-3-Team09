package ninegle.Readio.subscription.mapper

import ninegle.Readio.subscription.domain.Subscription
import ninegle.Readio.subscription.dto.response.SubscriptionResponseDto
import org.springframework.stereotype.Component

@Component
class SubscriptionMapper {
    fun toDto(subscription: Subscription): SubscriptionResponseDto =
        SubscriptionResponseDto(
            userId = subscription.userId,
            subDate = subscription.subDate,
            expDate = subscription.expDate,
            active = subscription.isActive(),
            canceled = subscription.canceled
        )
}