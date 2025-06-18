package ninegle.Readio.subscription.dto.response

import java.time.LocalDate

data class SubscriptionResponseDto(
    val userId: Long,
    val subDate: LocalDate,
    val expDate: LocalDate,
    val active: Boolean,
    val canceled: Boolean
) 