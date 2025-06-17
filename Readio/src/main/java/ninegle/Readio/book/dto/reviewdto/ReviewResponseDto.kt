package ninegle.Readio.book.dto.reviewdto

import java.math.BigDecimal
import java.time.LocalDateTime


data class ReviewResponseDto(
    val id: Long,
    val email: String,
    val rating: BigDecimal,
    val text: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
