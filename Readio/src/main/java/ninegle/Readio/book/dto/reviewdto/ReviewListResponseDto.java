package ninegle.Readio.book.dto.reviewdto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ninegle.Readio.book.dto.PaginationDto;

/**
 * Readio - ReviewListResponseDto
 * create date:    25. 5. 12.
 * last update:    25. 5. 12.
 * author:  gigol
 * purpose: 
 */
@Builder
public record ReviewListResponseDto(List<ReviewResponseDto> reviews, PaginationDto pagination,
									ReviewSummaryDto summary) {
}
