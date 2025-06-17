package ninegle.Readio.book.dto.reviewdto

import java.math.BigDecimal


data class ReviewSummaryDto(
    val averageRating: BigDecimal,
    val totalReviews: Int
)
