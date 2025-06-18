package ninegle.Readio.book.dto.reviewdto

import ninegle.Readio.book.dto.PaginationDto


data class ReviewListResponseDto(
    val reviews: List<ReviewResponseDto>,
    val pagination: PaginationDto,
    val summary: ReviewSummaryDto
)
